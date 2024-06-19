package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.tools.enums.DirectoryAlias;
import dev.scaraz.mars.common.utils.Util;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.agent.AgentWorkspace;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.AgentWorkspaceQueryService;
import dev.scaraz.mars.core.service.StorageService;
import dev.scaraz.mars.core.service.order.ExportService;
import dev.scaraz.mars.core.util.ExcelGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static dev.scaraz.mars.common.utils.AppConstants.ZONE_LOCAL;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final AccountQueryService accountQueryService;
//    private final AgentQueryService agentQueryService;
    private final AgentWorkspaceQueryService agentWorkspaceQueryService;
    private final StorageService storageService;


    @Override
    @Transactional(readOnly = true)
    public File exportTicketsToCSV(List<TicketSummary> all) throws IOException {
//        List<TicketSummary> all = ticketSummaryQueryService.findAll(criteria);

        List<String> rows = new ArrayList<>();
        rows.add(String.join(";", CSV_HEADER));

        Map<String, Account> accounts = accountQueryService.findAll().stream()
                .collect(Collectors.toMap(Account::getNik, a -> a));

        Instant currentTimestamp = Instant.now();
        for (TicketSummary s : all) {
            try {
                List<String> row = new ArrayList<>();

                row.add(s.getNo());
                row.add(s.getStatus().name());
                row.add(s.getWitel().name());
                row.add(s.getSto());
                row.add(s.getIncidentNo());
                row.add(s.getServiceNo());
                row.add(s.getSource().name());
                row.add(s.isGaul() ? "Y" : "N");
                row.add(Util.durationDescribe(Duration.ofMillis(
                        Optional.ofNullable(s.getClosedAt())
                                .map(Instant::toEpochMilli)
                                .orElseGet(currentTimestamp::toEpochMilli) - currentTimestamp.toEpochMilli()
                )));
                row.add(s.getSenderName());
                row.add(s.getIssue().getName());
                row.add(s.getIssue().getProduct().name());

                agentWorkspaceQueryService.getLastWorkspaceOptional(s.getId()).flatMap(AgentWorkspace::getLastWorklog)
                        .ifPresentOrElse(
                                wl -> {
                                    row.add(wl.getSolution().getName());
                                    row.add(wl.getMessage());
                                },
                                () -> {
                                    row.add(null);
                                    row.add(null);
                                });

                row.add(Optional.ofNullable(s.getCreatedBy())
                        .map(accounts::get)
                        .map(Account::getName)
                        .orElse("-"));
                row.add(s.getCreatedAt().atZone(ZONE_LOCAL).format(DATE_TIME_FORMAT));

                row.add(Optional.ofNullable(s.getUpdatedBy())
                        .map(accounts::get)
                        .map(Account::getName)
                        .orElse("-"));
                row.add(Optional.ofNullable(s.getUpdatedAt())
                        .map(t -> t.atZone(ZONE_LOCAL).format(DATE_TIME_FORMAT))
                        .orElse("-"));

                rows.add(String.join(";", row.stream()
                        .map(v -> v == null ? "" : v)
                        .toArray(String[]::new)));
            }
            catch (Exception ex) {
            }
        }
        File file = storageService.createFile(DirectoryAlias.TMP, "report", UUID.randomUUID() + ".csv");
        try (FileWriter wr = new FileWriter(file)) {
            for (String row : rows) {
                wr.write(row + "\n");
            }
            wr.flush();
        }

        return file;
    }

    @Override
    @Transactional(readOnly = true)
    public File exportTicketsToExcel(List<TicketSummary> all) throws IOException {
        File file = storageService.createFile(DirectoryAlias.TMP, "report", UUID.randomUUID() + ".xlsx");
        try (ExcelGenerator generator = new ExcelGenerator()) {
            ExcelGenerator.SheetGenerator sheet = generator.createSheet("Tiket");

            Map<String, Account> accounts = accountQueryService.findAll().stream()
                    .collect(Collectors.toMap(Account::getNik, a -> a));

            writeExcelHeader(sheet);
            for (int i = 0; i < all.size(); i++)
                writeExcelValue(sheet, i + 1, accounts, all.get(i));

            try (OutputStream os = new FileOutputStream(file)) {
                generator.write(os);
            }
        }
        catch (IOException ex) {
            FileUtils.deleteQuietly(file);
            throw ex;
        }

//        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
//            XSSFSheet sheet = workbook.createSheet("Tiket");
//
//            Map<String, Account> accounts = accountQueryService.findAll().stream()
//                    .collect(Collectors.toMap(Account::getNik, a -> a));
//
//            writeExcelHeader(workbook, sheet);
//            for (int i = 0; i < all.size(); i++)
//                writeExcelValue(workbook, sheet, i + 1, accounts, all.get(i));
//
//            for (int i = 0; i < CSV_HEADER.length; i++)
//                sheet.autoSizeColumn(i);
//
//            try (OutputStream os = new FileOutputStream(file)) {
//                workbook.write(os);
//            }
//        }
//        catch (IOException ex) {
//            FileUtils.deleteQuietly(file);
//            throw ex;
//        }
        return file;
    }

    private void writeExcelHeader(ExcelGenerator.SheetGenerator sheet) {
        ExcelGenerator.RowGenerator row = sheet.createRow(0);

        XSSFFont font = sheet.getGenerator().createFont();
        font.setBold(true);
        font.setFontHeight(16);

        XSSFCellStyle style = sheet.getGenerator().createCellStyle();
        style.setFont(font);

        for (int i = 0; i < CSV_HEADER.length; i++) {
            XSSFCell cell = row.createCell(i, style, CSV_HEADER[i]);
            CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);
        }
    }

    private void writeExcelValue(ExcelGenerator.SheetGenerator sheet,
                                 int rowIndex,
                                 Map<String, Account> accounts,
                                 TicketSummary summary) {
        ExcelGenerator.RowGenerator row = sheet.createRow(rowIndex);

        XSSFFont font = sheet.getGenerator().createFont();
        font.setFontHeight(14);

        XSSFCellStyle style = sheet.getGenerator().createCellStyle();
        style.setFont(font);


//        Instant currentTimestamp = Instant.now();
        row.createCell(0, style, summary.getNo());
        row.createCell(1, style, summary.getStatus());
        row.createCell(2, style, summary.getWitel());
        row.createCell(3, style, summary.getSto());
        row.createCell(4, style, summary.getIncidentNo());
        row.createCell(5, style, summary.getServiceNo());
        row.createCell(6, style, summary.getSource());
        row.createCell(7, style, summary.isGaul());
        row.createCell(8, style, summary.getAge()); // TTR
        row.createCell(9, style, summary.getAge().toMillis()); // TTR (ms)

        row.createCell(10, style, summary.getSenderName());
        row.createCell(11, style, summary.getIssue().getName());
        row.createCell(12, style, summary.getIssue().getProduct());

        // 12 & 13
        agentWorkspaceQueryService.getLastWorkspaceOptional(summary.getId())
                .flatMap(AgentWorkspace::getLastWorklog)
                .ifPresentOrElse(
                        wl -> {
                            if (wl.getSolution() == null)
                                row.createEmptyCell(13, style);
                            else
                                row.createCell(13, style, wl.getSolution().getName());

                            row.createCell(14, style, wl.getMessage());
                        },
                        () -> {
                            row.createEmptyCell(13, style);
                            row.createEmptyCell(14, style);
                        }
                );

        row.createCell(15, style, Optional.ofNullable(accounts.get(summary.getCreatedBy()))
                .map(Account::getName)
                .orElse("-"));
        row.createCell(16, style, summary.getCreatedAt());

        row.createCell(17, style, Optional.ofNullable(accounts.get(summary.getUpdatedBy()))
                .map(Account::getName)
                .orElse("-"));
        row.createCell(18, style, summary.getUpdatedAt());
    }

    private static final String[] CSV_HEADER = {
            "No",
            "Status",
            "Witel",
            "STO",
            "No Tiket",
            "No Service",
            "Source",
            "Gaul",
            "TTR",
            "TTR (ms)",
            "Requestor",
            "Isu/Masalah",
            "Produk",
            "Solusi",
            "Pesan",
            "Dibuat Oleh",
            "Tgl Dibuat",
            "Diubah Oleh",
            "Tgl Diubah",
    };

}
