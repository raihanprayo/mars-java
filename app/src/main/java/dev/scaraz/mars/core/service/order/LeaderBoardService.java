package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.response.LeaderBoardDTO;
import dev.scaraz.mars.common.tools.enums.DirectoryAlias;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.*;
import dev.scaraz.mars.common.utils.AuthorityConstant;
import dev.scaraz.mars.common.utils.ConfigConstants;
import dev.scaraz.mars.core.domain.agent.Leaderboard;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.LeaderboardQueryService;
import dev.scaraz.mars.core.query.criteria.AccountCriteria;
import dev.scaraz.mars.core.query.criteria.LeaderboardCriteria;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
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
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class LeaderBoardService {

    private final ConfigService configService;

    private final AccountQueryService accountQueryService;
    private final StorageService storageService;

    private final LeaderboardQueryService leaderboardQueryService;

    @Transactional(readOnly = true)
    public List<LeaderBoardDTO> leaderboardSummary(LeaderboardCriteria criteria) {
        List<Long> ignored = getIgnoredSolutions();
        List<Leaderboard> summaries = getFragments(criteria);
        Map<String, LeaderBoardDTO> result = new HashMap<>();

        for (Leaderboard summary : summaries) {
            if (ignored.contains(summary.getSolutionId())) continue;

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

//            if (summary.getTakeStatus() == TcStatus.DISPATCH)
//                data.incrementTotalHandleDispatch();
//            if (summary.getCloseStatus() == TcStatus.DISPATCH)
//                data.incrementTotalDispatch();

            if (summary.getDurationAction() != null)
                data.increaseTotalActionDuration(summary.getDurationAction().toMillis());
            if (summary.getDurationResponse() != null)
                data.increaseTotalResponseDuration(summary.getDurationResponse().toMillis());


            if (summary.getCloseStatus() == TcStatus.DISPATCH)
                data.sumTotalScore(-0.5);
            else {
                double score = summary.getScore();
                double scoreResponse = scoreResponse(summary.getDurationResponse());
                double scoreAction = scoreAction(summary.getDurationAction());

                double finalScore = score * scoreResponse * scoreAction;

                data.sumTotalScore(finalScore);
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

                    data.setTotalDispatch(leaderboardQueryService.count(criteria.dup()
                            .setLastAgentWork(null)
                            .setAgId(new StringFilter()
                                    .setEq(data.getId()))
                            .setCloseStatus(new TcStatusFilter()
                                    .setEq(TcStatus.DISPATCH))
                    ));
                    data.setTotalHandleDispatch(leaderboardQueryService.count(criteria.dup()
                            .setLastAgentWork(null)
                            .setAgId(new StringFilter()
                                    .setEq(data.getId()))
                            .setTakeStatus(new TcStatusFilter()
                                    .setEq(TcStatus.DISPATCH))
                    ));
                })
                .toList();
    }


    @Transactional(readOnly = true)
    public File exportToExcel(LeaderboardCriteria criteria) throws IOException {
        File file = storageService.createFile(DirectoryAlias.TMP, "report", UUID.randomUUID() + ".xlsx");

        AtomicInteger rowIndex = new AtomicInteger();
        try (ExcelGenerator generator = new ExcelGenerator()) {
            ExcelGenerator.SheetGenerator sheet = generator.createSheet("All");

            List<Leaderboard> summaries = getFragments(criteria);
            for (Leaderboard summary : summaries) {
                ExcelGenerator.RowGenerator row = sheet.createRow(rowIndex.getAndIncrement());
                writeSheetValue(row, summary);
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


    private double scoringByInterval(Duration interval,
                                     double radius,
                                     long everySec,
                                     long maxSec
    ) {
        if (interval == null) return 0;

        long ms = interval.toMillis();
        for (int i = 0; ; i++) {
            long msToCompare = everySec * (i + 1) * 1000;
            double result = BigDecimal.valueOf((1.0 - (radius * i)))
                    .round(new MathContext(2, RoundingMode.HALF_DOWN))
                    .doubleValue();

            if (ms <= msToCompare)
                return result;

            if (msToCompare >= maxSec)
                break;
        }

        return radius;
    }

    private double scoreAction(Duration interval) {
        long sec = 900;
        return scoringByInterval(interval, 0.2, sec, sec * 4);
    }

    private double scoreResponse(Duration interval) {
        long sec = 300;
        return scoringByInterval(interval, 0.1, sec, sec * 6);
    }


    private List<Long> getIgnoredSolutions() {
        return configService.get(ConfigConstants.APP_SOLUTION_REPORT_EXCLUDE_LIST)
                .getAsLongList();
    }

    private List<Account> getAccounts(WitelFilter witelFilter) {
        return accountQueryService.findAll(new AccountCriteria()
                        .setWitel(witelFilter)
                        .setRoles(new RoleCriteria()
                                .setName(new StringFilter().setEq(AuthorityConstant.AGENT_ROLE))),
                Sort.by("name"));
    }

    private List<Leaderboard> getFragments(LeaderboardCriteria criteria) {
        List<String> accounts = getAccounts(criteria.getAgWitel()).stream()
                .map(Account::getId)
                .toList();

        criteria.setLastAgentWork(new BooleanFilter()
                        .setEq(true))
                .setAgId(new StringFilter()
                        .setIn(accounts));

        return leaderboardQueryService.findAll(criteria);
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

        // Skor Tiket
        double score = summary.getScore();
        row.createCell(i.getAndIncrement(), style, score);

        // Tgl Tiket Dibuat
        row.createCell(i.getAndIncrement(), style, summary.getTcCreatedAt());

        // Pengerjaan Terakhir (Y/N)
        row.createCell(i.getAndIncrement(), style, summary.isLastTicketWork());

        // Skor Respon
        double responseScore = scoreResponse(summary.getDurationResponse());
        row.createCell(i.getAndIncrement(), style, responseScore);

        // Lama Respon
        row.createCell(i.getAndIncrement(), style, summary.getDurationResponse());

        // Skor Aksi
        double actionScore = scoreAction(summary.getDurationAction());
        row.createCell(i.getAndIncrement(), style, actionScore);

        // Lama Aksi
        row.createCell(i.getAndIncrement(), style, summary.getDurationAction());

        // Overall Score
        if (summary.getCloseStatus() == TcStatus.DISPATCH)
            row.createCell(i.getAndIncrement(), style, -0.5);
        else
            row.createCell(i.getAndIncrement(), style, score * responseScore * actionScore);
    }

    private static final String[] LEADERBOARD_RAW_HEADER = {
            "No",
            "NIK Requestor",
            "NIK Eksekutor",
            "Nama Eksekutor",

            "Tiket",
            "Skor Tiket",
            "Tgl Tiket Dibuat",

            "Pengerjaan Terakhir (Y/N)",

            "Skor Respon",
            "Lama Respon",
//            "Lama Respon (ms)",
            "Skor Aksi",
            "Lama Aksi",

            "Overall Skor"
    };
}
