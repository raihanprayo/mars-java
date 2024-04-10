package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.response.LeaderBoardDTO;
import dev.scaraz.mars.common.tools.enums.DirectoryAlias;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.AuthorityConstant;
import dev.scaraz.mars.common.utils.ConfigConstants;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.order.Agent;
import dev.scaraz.mars.core.domain.order.Solution;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.order.WlSolution;
import dev.scaraz.mars.core.domain.view.LeaderBoardFragment;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.AgentQueryService;
import dev.scaraz.mars.core.query.SolutionQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.criteria.LeaderBoardCriteria;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
import dev.scaraz.mars.core.query.criteria.SolutionCriteria;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.query.spec.LeaderBoardSpecBuilder;
import dev.scaraz.mars.core.repository.db.order.AgentRepo;
import dev.scaraz.mars.core.repository.db.view.LeaderBoardFragmentRepo;
import dev.scaraz.mars.core.service.ConfigService;
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
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class LeaderBoardService {

    private final ConfigService configService;

    private final AccountQueryService accountQueryService;
    private final TicketQueryService ticketQueryService;

    private final AgentRepo agentRepo;
    private final AgentQueryService agentQueryService;

    private final LeaderBoardFragmentRepo repo;
    private final LeaderBoardSpecBuilder specBuilder;

    private final SolutionQueryService solutionQueryService;
    private final StorageService storageService;


    public List<LeaderBoardDTO> getLeaderboard(LeaderBoardCriteria criteria) {
        List<Account> accounts = getAccounts();
        List<Long> solutionsId = configService.get(ConfigConstants.APP_SOLUTION_REPORT_EXCLUDE_LIST)
                .getAsLongList();

        if (solutionsId != null && !solutionsId.isEmpty()) {
            List<Solution> solutions = solutionQueryService.findAll(SolutionCriteria.builder()
                    .id(new LongFilter().setIn(solutionsId))
                    .build());

            criteria.setSolution(new SolutionCriteria()
                    .setId(new LongFilter().setNegated(true).setIn(solutions.stream()
                            .map(Solution::getId)
                            .toList()))
            );
        }

        return accounts.stream()
                .map(user -> this.getLeaderboard(user, criteria, false))
                .collect(Collectors.toList());
    }

    public List<LeaderBoardFragment> getLeaderboardFragments(LeaderBoardCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria), Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private LeaderBoardDTO getLeaderboard(Account user, LeaderBoardCriteria criteria, boolean includeFragmentAsResult) {
        criteria.setCreatedBy(new StringFilter().setEq(user.getNik()));
        List<LeaderBoardFragment> fragments = repo.findAll(specBuilder.createSpec(criteria));

        Duration avgAction = getAverageDuration(fragments, LeaderBoardFragment::getActionDuration);
        Duration avgResponse = getAverageDuration(fragments, LeaderBoardFragment::getResponseDuration);

        long totalHandleDispatch = fragments.stream()
                .filter(frg -> frg.getStart() == TcStatus.DISPATCH)
                .count();

        long totalDispatch = fragments.stream()
                .filter(frg -> frg.getClose() == TcStatus.DISPATCH)
                .count();

        Set<String> ticketIds = fragments.stream()
                .map(LeaderBoardFragment::getTicketId)
                .collect(Collectors.toSet());

        double totalScore = ticketQueryService.sumTotalScore(ticketIds);

        List<LeaderBoardFragment> includeFragments = includeFragmentAsResult ? fragments : null;
        return LeaderBoardDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .nik(user.getNik())
                .avgAction(avgAction)
                .avgResponse(avgResponse)
                .total(ticketIds.size())
                .totalScore(totalScore)
                .totalDispatch(totalDispatch)
                .totalHandleDispatch(totalHandleDispatch)
                .build();
    }

    private Duration getAverageDuration(Collection<LeaderBoardFragment> fragments, Function<LeaderBoardFragment, Duration> map) {
        List<Duration> list = fragments.stream().map(map).filter(Objects::nonNull).toList();
        if (list.isEmpty()) return Duration.ZERO;
        return Duration.ofMillis(list.stream()
                .map(Duration::toMillis)
                .reduce(Long::sum)
                .map(l -> l / list.size())
                .orElse(0L));
    }

    private List<Account> getAccounts() {
        return accountQueryService.findAll(UserCriteria.builder()
                .roles(RoleCriteria.builder()
                        .name(new StringFilter().setEq(AuthorityConstant.AGENT_ROLE))
                        .build())
                .build(), Sort.by("name"));
    }

    @Transactional(readOnly = true)
    public File exportToExcel(LeaderBoardCriteria criteria) throws IOException {
        File file = storageService.createFile(DirectoryAlias.TMP, "report", UUID.randomUUID() + ".xlsx");
        List<Account> accounts = getAccounts();
        Map<String, String> userAgentIds = accounts.stream()
                .map(Account::getId)
                .map(agentRepo::findByUserId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(Agent::getUserId, Agent::getId));


        Map<String, Ticket> cacheTicket = new LinkedHashMap<>();

        AtomicInteger rowIndex = new AtomicInteger(1);
        try (ExcelGenerator generator = new ExcelGenerator()) {
            ExcelGenerator.SheetGenerator sheet = generator.createSheet("All");

            for (Account account : accounts) {
                criteria.setAgentId(new StringFilter().setEq(userAgentIds.get(account.getId())));

                List<LeaderBoardFragment> fragments = getLeaderboardFragments(criteria);

                for (LeaderBoardFragment fragment : fragments) {
                    Ticket ticket;
                    if (cacheTicket.containsKey(fragment.getTicketId()))
                        ticket = cacheTicket.get(fragment.getTicketId());
                    else {
                        ticket = ticketQueryService.findByIdOrNo(fragment.getTicketId());
                        cacheTicket.put(ticket.getId(), ticket);
                    }

                    ExcelGenerator.RowGenerator row = sheet.createRow(rowIndex.getAndIncrement());
                    writeSheetValue(row, account, ticket, fragment);
                }
            }
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

    private void writeSheetValue(ExcelGenerator.RowGenerator row,
                                 Account account,
                                 Ticket ticket,
                                 LeaderBoardFragment fragment) {

        XSSFFont font = row.getSheet().getGenerator().createFont();
        font.setFontHeight(14);

        XSSFCellStyle style = row.getSheet().getGenerator().createCellStyle();
        style.setFont(font);

        Function<Duration, Long> drtToMs = d -> Optional.ofNullable(d)
                .map(Duration::toMillis)
                .orElse(null);


        row.createCell(0, style, row.getRowNum());
        row.createCell(1, style, account.getNik());
        row.createCell(2, style, account.getName());
        row.createCell(3, style, ticket.getNo());
        row.createCell(4, style, ticket.getIssue().getProduct());
        row.createCell(5, style, ticket.getIssue().getScore());
        row.createCell(6, style, Optional.ofNullable(fragment.getSolution())
                .map(WlSolution::getName)
                .orElse(null));

        row.createCell(7, style, fragment.getStart());
        row.createCell(8, style, fragment.getClose());

        row.createCell(9, style, fragment.getResponseDuration());
        row.createCell(10, style, drtToMs.apply(fragment.getResponseDuration()));
        row.createCell(11, style, fragment.getActionDuration());
        row.createCell(12, style, drtToMs.apply(fragment.getActionDuration()));
    }

    private static final String[] LEADERBOARD_RAW_HEADER = {
            "No",
            "NIK",
            "Nama",
            "No Tiket",
            "Produk",
            "Skor",
            "Solusi",

            "Status Pengambilan",
            "Status Penyelesaian",

            "Lama Respon",
            "Lama Respon (ms)",
            "Lama Aksi",
            "Lama Aksi (ms)",
    };
}
