package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.response.LeaderBoardDTO;
import dev.scaraz.mars.common.tools.enums.DirectoryAlias;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.AuthorityConstant;
import dev.scaraz.mars.common.utils.ConfigConstants;
import dev.scaraz.mars.core.domain.agent.Leaderboard;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.query.*;
import dev.scaraz.mars.core.query.criteria.AccountCriteria;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
import dev.scaraz.mars.core.query.criteria.LeaderboardCriteria;
import dev.scaraz.mars.core.service.ConfigService;
import dev.scaraz.mars.core.service.LogDownloadService;
import dev.scaraz.mars.core.service.StorageService;
import dev.scaraz.mars.core.util.ExcelGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class LeaderBoardService {

    private final ConfigService configService;

    private final AccountQueryService accountQueryService;
    private final StorageService storageService;

    private final LogDownloadService logDownloadService;
    private final LeaderboardQueryService leaderboardQueryService;

//    public List<LeaderBoardDTO> getLeaderboard(LeaderBoardCriteria criteria) {
//        List<Account> accounts = getAccounts();
//        List<Long> solutionsId = configService.get(ConfigConstants.APP_SOLUTION_REPORT_EXCLUDE_LIST)
//                .getAsLongList();
//
//        if (solutionsId != null && !solutionsId.isEmpty()) {
//            List<Solution> solutions = solutionQueryService.findAll(SolutionCriteria.builder()
//                    .id(new LongFilter().setIn(solutionsId))
//                    .build());
//
//            criteria.setSolution(new SolutionCriteria()
//                    .setId(new LongFilter().setNegated(true).setIn(solutions.stream()
//                            .map(Solution::getId)
//                            .toList()))
//            );
//        }
//
//        return accounts.stream()
//                .map(user -> this.getLeaderboard(user, criteria))
//                .collect(Collectors.toList());
//    }

    @Transactional(readOnly = true)
    public List<LeaderBoardDTO> getLeaderboard(LeaderboardCriteria criteria) {
        List<Account> accounts = getAccounts();
        List<Long> solutionsId = configService.get(ConfigConstants.APP_SOLUTION_REPORT_EXCLUDE_LIST)
                .getAsLongList();

        criteria.setSolutionId(new LongFilter()
                .setNegated(true)
                .setIn(solutionsId));

        List<Leaderboard> summaries = leaderboardQueryService.findAll(criteria);
        Map<String, LeaderBoardDTO> result = accounts.stream().collect(Collectors.toMap(
                Account::getId,
                u -> LeaderBoardDTO.builder()
                        .id(u.getId())
                        .nik(u.getNik())
                        .name(u.getName())
                        .build()
        ));

        for (Leaderboard summary : summaries) {
            LeaderBoardDTO data;
            if (result.containsKey(summary.getAgId()))
                data = result.get(summary.getAgId());
            else {
                data = new LeaderBoardDTO();
                result.put(summary.getAgId(), data);

                data.setId(summary.getAgId());
                data.setNik(summary.getAgNik());
                data.setName(summary.getAgName());
            }

            data.incrementTotal();

            if (summary.getTakeStatus() == TcStatus.DISPATCH)
                data.incrementTotalHandleDispatch();
            if (summary.getCloseStatus() == TcStatus.DISPATCH)
                data.incrementTotalDispatch();

            if (summary.getDurationAction() != null)
                data.increaseTotalActionDuration(summary.getDurationAction().toMillis());
            if (summary.getDurationResponse() != null)
                data.increaseTotalResponseDuration(summary.getDurationResponse().toMillis());


            double score = summary.getScore();

            data.sumTotalScore(scoringByInterval(
                    score, summary.getDurationResponse(), 0.1, 300_000, 6));

            if (summary.getCloseStatus() == TcStatus.DISPATCH)
                data.sumTotalScore(-1);
            else {
                data.sumTotalScore(scoringByInterval(
                        score, summary.getDurationAction(), 0.2, 900_000, 4));
            }
        }

        return result.values().stream()
                .peek(data -> {
                    if (data.getTotalDurationAction() != 0)
                        data.setAvgAction(Duration.ofMillis(data.getTotalDurationAction() / data.getTotalDivideAction()));

                    if (data.getTotalDivideResponse() != 0)
                        data.setAvgResponse(Duration.ofMillis(data.getTotalDurationResponse() / data.getTotalDivideResponse()));

                    data.setTotalScore(new BigDecimal(String.valueOf(data.getTotalScore()))
                            .setScale(3, RoundingMode.HALF_DOWN)
                            .doubleValue());
                })
                .toList();
    }
//
//    public List<LeaderBoardFragment> getLeaderboardFragments(LeaderBoardCriteria criteria) {
//        return repo.findAll(specBuilder.createSpec(criteria), Sort.by(Sort.Direction.DESC, "createdAt"));
//    }

    private double scoringByInterval(double score,
                                     Duration interval,
                                     double radius,
                                     long everyMs,
                                     int loop
    ) {
        if (interval == null) return 0;

        long ms = interval.toMillis();
        for (int i = 0; i < loop; i++) {
            long msToCompare = everyMs * (i + 1);
            double db = BigDecimal.valueOf((1.0 - (radius * i)))
                    .round(new MathContext(2, RoundingMode.HALF_DOWN))
                    .doubleValue();

//            log.debug("Compare {} - {}", Duration.ofMillis(msToCompare), db);
            if (ms <= msToCompare) {
                BigDecimal result = BigDecimal.valueOf(score * db)
                        .round(new MathContext(
                                2,
                                RoundingMode.HALF_DOWN));

                return result.doubleValue();
            }
        }

        return radius;
    }

//    private LeaderBoardDTO getLeaderboard(Account user, LeaderBoardCriteria criteria) {
//        criteria.setCreatedBy(new StringFilter().setEq(user.getNik()));
//        List<LeaderBoardFragment> fragments = repo.findAll(specBuilder.createSpec(criteria));
//
//        Duration avgAction = averageInterval(fragments, LeaderBoardFragment::getActionDuration);
//        Duration avgResponse = averageInterval(fragments, LeaderBoardFragment::getResponseDuration);
//
//        long totalHandleDispatch = fragments.stream()
//                .filter(frg -> frg.getStart() == TcStatus.DISPATCH)
//                .count();
//
//        long totalDispatch = fragments.stream()
//                .filter(frg -> frg.getClose() == TcStatus.DISPATCH)
//                .count();
//
//        Set<String> ticketIds = fragments.stream()
//                .map(LeaderBoardFragment::getTicketId)
//                .collect(Collectors.toSet());
//
//        double totalTicketScore = ticketQueryService.sumTotalScore(ticketIds);
//        double totalScore = totalTicketScore - (totalDispatch * 0.1) + (totalHandleDispatch * 0.1);
//
//        return LeaderBoardDTO.builder()
//                .id(user.getId())
//                .name(user.getName())
//                .nik(user.getNik())
//                .avgAction(avgAction)
//                .avgResponse(avgResponse)
//                .total(ticketIds.size())
//                .totalScore(totalScore)
//                .totalDispatch(totalDispatch)
//                .totalHandleDispatch(totalHandleDispatch)
//                .build();
//    }

//    private <T> Duration averageInterval(Collection<T> fragments, Function<T, Duration> map) {
//        List<Duration> list = fragments.stream().map(map).filter(Objects::nonNull).toList();
//        if (list.isEmpty()) return Duration.ZERO;
//        return Duration.ofMillis(list.stream()
//                .map(Duration::toMillis)
//                .reduce(Long::sum)
//                .map(l -> l / list.size())
//                .orElse(0L));
//    }

    private List<Account> getAccounts() {
        return accountQueryService.findAll(
                new AccountCriteria()
                        .setRoles(new RoleCriteria()
                                .setName(new StringFilter().setEq(AuthorityConstant.AGENT_ROLE))),
                Sort.by("name"));
    }

//    @Transactional(readOnly = true)
//    public File exportToExcel(LeaderBoardCriteria criteria) throws IOException {
//        File file = storageService.createFile(DirectoryAlias.TMP, "report", UUID.randomUUID() + ".xlsx");
//        List<Account> accounts = getAccounts();
//
//        Map<String, String> userAgentIds = accounts.stream()
//                .map(Account::getId)
//                .map(agentRepo::findByUserId)
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .collect(Collectors.toMap(Agent::getUserId, Agent::getId));
//
//        Map<Long, Account> userRequestorIds = new LinkedHashMap<>();
//
//
//        Map<String, Ticket> cacheTicket = new LinkedHashMap<>();
//        Function<String, Ticket> storeAndGetTicketCache = id -> {
//            if (cacheTicket.containsKey(id))
//                return cacheTicket.get(id);
//
//            Ticket ticket = ticketQueryService.findByIdOrNo(id);
//            cacheTicket.put(id, ticket);
//            return ticket;
//        };
//
//        AtomicInteger rowIndex = new AtomicInteger(1);
//        try (ExcelGenerator generator = new ExcelGenerator()) {
//            ExcelGenerator.SheetGenerator sheet = generator.createSheet("All");
//
//            for (Account account : accounts) {
//                criteria.setUserId(new StringFilter().setEq(account.getId()));
//
////                List<LeaderBoardFragment> fragments = getLeaderboardFragments(criteria);
//
//
//                List<AgentWorkspace> workspaces = agentWorkspaceQueryService.findAll(new AgentWorkspaceCriteria()
//                                .setAccount(new AccountCriteria()
//                                        .setId(new StringFilter().setEq(account.getId())))
////                        .setAgent(new AgentCriteria()
////                                .setUserId(new StringFilter().setEq(account.getId()))
////                        )
//                                .setTicket(new TicketCriteria()
//                                        .setCreatedAt(criteria.getCreatedAt()))
//                );
//
//                for (AgentWorkspace ws : workspaces) {
//                    Ticket ticket = ws.getTicket();
//                    Account requestor;
//
//                    if (userRequestorIds.containsKey(ticket.getSenderId()))
//                        requestor = userRequestorIds.get(ticket.getSenderId());
//                    else {
//                        Optional<Account> opt = accountQueryService.findByTelegramIdOpt(ticket.getSenderId());
//                        if (opt.isPresent()) {
//                            requestor = opt.get();
//                            userRequestorIds.put(ticket.getSenderId(), requestor);
//                        }
//                        else {
//                            requestor = null;
//                            userRequestorIds.put(ticket.getSenderId(), null);
//                        }
//                    }
//
//                    for (AgentWorklog worklog : ws.getWorklogs()) {
//                        ExcelGenerator.RowGenerator row = sheet.createRow(rowIndex.getAndIncrement());
//                        writeSheetValue(row, account, requestor, ticket, worklog);
//                    }
//                }
//
////                for (LeaderBoardFragment fragment : fragments) {
////                    Ticket ticket = storeAndGetTicketCache.apply(fragment.getTicketId());
////                    if (cacheTicket.containsKey(fragment.getTicketId()))
////                        ticket = cacheTicket.get(fragment.getTicketId());
////                    else {
////                        ticket = ticketQueryService.findByIdOrNo(fragment.getTicketId());
////                        cacheTicket.put(ticket.getId(), ticket);
////                    }
////
////
////                    ExcelGenerator.RowGenerator row = sheet.createRow(rowIndex.getAndIncrement());
////                    writeSheetValue(row, account, ticket, fragment);
////                }
//            }
//
//            // Penulisan header excel di akhir dikarenakan ada penyesuaian lebar kolom
//            writeSheetHeaderValue(sheet);
//
//            try (OutputStream os = new FileOutputStream(file)) {
//                generator.write(os);
//            }
//
//            userRequestorIds.clear();
//            return file;
//        }
//        catch (IOException e) {
//            FileUtils.deleteQuietly(file);
//            throw e;
//        }
//    }

    @Transactional(readOnly = true)
    public File exportToExcel(LeaderboardCriteria criteria) throws IOException {
        File file = storageService.createFile(DirectoryAlias.TMP, "report", UUID.randomUUID() + ".xlsx");
        List<Account> accounts = getAccounts();

        AtomicInteger rowIndex = new AtomicInteger();
        try (ExcelGenerator generator = new ExcelGenerator()) {
            ExcelGenerator.SheetGenerator sheet = generator.createSheet("All");

            List<Long> solutionsId = configService.get(ConfigConstants.APP_SOLUTION_REPORT_EXCLUDE_LIST)
                    .getAsLongList();

            criteria.setSolutionId(new LongFilter()
                    .setNegated(true)
                    .setIn(solutionsId));
            List<Leaderboard> summaries = leaderboardQueryService.findAll(criteria);

            for (Account account : accounts) {
                List<Leaderboard> workSummaries = summaries.stream()
                        .filter(e -> e.getAgId().equals(account.getId()))
                        .toList();

                for (Leaderboard summary : workSummaries) {
                    summaries.removeIf(s -> s.getId() == summary.getId());

                    ExcelGenerator.RowGenerator row = sheet.createRow(rowIndex.getAndIncrement());
                    writeSheetValue(row, summary);
                }
            }

            // Penulisan header excel di akhir dikarenakan ada penyesuaian lebar kolom
            writeSheetHeaderValue(sheet);

            try (OutputStream os = new FileOutputStream(file)) {
                generator.write(os);
            }

            return file;
        }
        catch (IOException e) {
            FileUtils.deleteQuietly(file);
            throw e;
        }
    }


    private void writeSheetHeaderValue(ExcelGenerator.SheetGenerator sheet) {
        ExcelGenerator.RowGenerator row = sheet.createRow(0);

        XSSFFont font = sheet.getGenerator().createFont();
        font.setBold(true);
        font.setFontHeight(16);

        XSSFCellStyle style = sheet.getGenerator().createCellStyle();
        style.setFont(font);

        for (int i = 0; i < LEADERBOARD_RAW_HEADER.length; i++) {
            String header = LEADERBOARD_RAW_HEADER[i];
            XSSFCell cell = row.createCell(i, style, header);
            CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);

            if (i != 6)
                sheet.autoSizeColumn(i);
        }
    }

//    private void writeSheetValue(ExcelGenerator.RowGenerator row,
//                                 Account agent,
//                                 @Nullable Account requestor,
//                                 Ticket ticket,
//                                 AgentWorklog worklog
//    ) {
//
//        XSSFFont font = row.getSheet().getGenerator().createFont();
//        font.setFontHeight(14);
//
//        XSSFCellStyle style = row.getSheet().getGenerator().createCellStyle();
//        style.setFont(font);
//
//        LeaderBoardFragment fragment = repo.findById(worklog.getId())
//                .orElseThrow(() -> new BadRequestException("unknown fragment id"));
//
//        Function<Duration, Long> drtToMs = d -> Optional.ofNullable(d)
//                .map(Duration::toMillis)
//                .orElse(null);
//
//        AtomicInteger i = new AtomicInteger(0);
//
//        row.createCell(i.getAndIncrement(), style, row.getRowNum());
//
//        row.createCell(i.getAndIncrement(), style, Optional.ofNullable(requestor)
//                .map(Account::getNik)
//                .orElse("<tidak ditemukan>"));
//
//        row.createCell(i.getAndIncrement(), style, agent.getNik());
//        row.createCell(i.getAndIncrement(), style, agent.getName());
//
//        row.createCell(i.getAndIncrement(), style, ticket.getNo());
//        row.createCell(i.getAndIncrement(), style, ticket.getIssue().getScore());
//
//
//        ArrayList<AgentWorklog> lastWorkspacesTicket = new ArrayList<>(ticket.getWorkspaces().getLast().getWorklogs());
//        row.createCell(i.getAndIncrement(), style, lastWorkspacesTicket.getLast().getId() == worklog.getId());
//
//        // Lama Respon
//        row.createCell(i.getAndIncrement(), style, fragment.getResponseDuration());
//        // Lama Respon (ms)
//        row.createCell(i.getAndIncrement(), style, drtToMs.apply(fragment.getResponseDuration()));
//        // Lama Aksi
//        row.createCell(i.getAndIncrement(), style, fragment.getActionDuration());
//        // Lama Aksi (ms)
//        row.createCell(i.getAndIncrement(), style, drtToMs.apply(fragment.getActionDuration()));
//    }

    private void writeSheetValue(ExcelGenerator.RowGenerator row, Leaderboard summary) {
        XSSFFont font = row.getSheet().getGenerator().createFont();
        font.setFontHeight(14);

        XSSFCellStyle style = row.getSheet().getGenerator().createCellStyle();
        style.setFont(font);

//        LeaderBoardFragment fragment = repo.findById(worklog.getId())
//                .orElseThrow(() -> new BadRequestException("unknown fragment id"));
//
//        Function<Duration, Long> drtToMs = d -> Optional.ofNullable(d)
//                .map(Duration::toMillis)
//                .orElse(null);

        AtomicInteger i = new AtomicInteger(0);

        row.createCell(i.getAndIncrement(), style, row.getRowNum());

        row.createCell(i.getAndIncrement(), style, Optional.ofNullable(summary.getRqNik())
                .orElse("<tidak ditemukan>"));

        row.createCell(i.getAndIncrement(), style, summary.getAgNik());
        row.createCell(i.getAndIncrement(), style, summary.getAgName());

        row.createCell(i.getAndIncrement(), style, summary.getTicketNo());
        row.createCell(i.getAndIncrement(), style, summary.getScore());

        row.createCell(i.getAndIncrement(), style, summary.isLastWork());

        // Skor Respon
        row.createCell(i.getAndIncrement(), style, scoringByInterval(summary.getScore(), summary.getDurationResponse(), 0.1, 300_000, 6));
        // Lama Respon
        row.createCell(i.getAndIncrement(), style, summary.getDurationResponse());

        // Skor Aksi
        row.createCell(i.getAndIncrement(), style, scoringByInterval(summary.getScore(), summary.getDurationResponse(), 0.2, 900_000, 4));
        // Lama Aksi
        row.createCell(i.getAndIncrement(), style, summary.getDurationAction());
    }

    private static final String[] LEADERBOARD_RAW_HEADER = {
            "No",
            "NIK Requestor",
            "NIK Eksekutor",
            "Nama Eksekutor",

            "Tiket",
            "Skor Tiket",

            "Pengerjaan Terakhir (Y/N)",

            "Skor Respon",
            "Lama Respon",
//            "Lama Respon (ms)",
            "Skor Aksi",
            "Lama Aksi",
//            "Lama Aksi (ms)",
    };
}
