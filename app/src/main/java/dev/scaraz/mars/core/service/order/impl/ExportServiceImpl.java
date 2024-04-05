package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.tools.enums.DirectoryAlias;
import dev.scaraz.mars.common.utils.Util;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.AgentQueryService;
import dev.scaraz.mars.core.service.StorageService;
import dev.scaraz.mars.core.service.order.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.*;
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
    private final AgentQueryService agentQueryService;
    private final StorageService storageService;


    @Override
    @Transactional(readOnly = true)
    public File exportToCSV(List<TicketSummary> all) throws IOException {
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

                agentQueryService.getLastWorkspaceOptional(s.getId()).flatMap(AgentWorkspace::getLastWorklog)
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
    public File exportToExcel(List<TicketSummary> all) throws IOException {
        File file = storageService.createFile(DirectoryAlias.TMP, "report", UUID.randomUUID() + ".xlsx");
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Tiket");

            Map<String, Account> accounts = accountQueryService.findAll().stream()
                    .collect(Collectors.toMap(Account::getNik, a -> a));

            writeExcelHeader(workbook, sheet);
            for (int i = 0; i < all.size(); i++)
                writeExcelValue(workbook, sheet, i + 1, accounts, all.get(i));

            for (int i = 0; i < CSV_HEADER.length; i++)
                sheet.autoSizeColumn(i);

            try (OutputStream os = new FileOutputStream(file)) {
                workbook.write(os);
            }
        }
        catch (IOException ex) {
            FileUtils.deleteQuietly(file);
            throw ex;
        }
        return file;
    }

    private void writeExcelHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(0);

        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);

        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);

        for (int i = 0; i < CSV_HEADER.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            cell.setCellValue(CSV_HEADER[i]);
            CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);
        }
    }

    private void writeExcelValue(XSSFWorkbook workbook,
                                 XSSFSheet sheet,
                                 int rowIndex,
                                 Map<String, Account> accounts,
                                 TicketSummary summary) {
        XSSFRow row = sheet.createRow(rowIndex);

        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);

        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);


        Instant currentTimestamp = Instant.now();
        createCell(row, 0, style, summary.getNo());
        createCell(row, 1, style, summary.getStatus());
        createCell(row, 2, style, summary.getWitel());
        createCell(row, 3, style, summary.getSto());
        createCell(row, 4, style, summary.getIncidentNo());
        createCell(row, 5, style, summary.getServiceNo());
        createCell(row, 6, style, summary.getSource());
        createCell(row, 7, style, summary.isGaul());
        createCell(row, 8, style, Duration.ofMillis(
                Optional.ofNullable(summary.getClosedAt())
                        .map(Instant::toEpochMilli)
                        .orElseGet(currentTimestamp::toEpochMilli) - currentTimestamp.toEpochMilli()
        )); // TTR
        createCell(row, 9, style, summary.getSenderName());
        createCell(row, 10, style, summary.getIssue().getName());
        createCell(row, 11, style, summary.getIssue().getProduct());

        // 12 & 13
        agentQueryService.getLastWorkspaceOptional(summary.getId())
                .flatMap(AgentWorkspace::getLastWorklog)
                .ifPresentOrElse(
                        wl -> {
                            if (wl.getSolution() == null)
                                createEmptyCell(row, 12, style);
                            else
                                createCell(row, 12, style, wl.getSolution().getName());

                            createCell(row, 13, style, wl.getMessage());
                        },
                        () -> {
                            createEmptyCell(row, 12, style);
                            createEmptyCell(row, 13, style);
                        }
                );

        createCell(row, 14, style, Optional.ofNullable(accounts.get(summary.getCreatedBy()))
                .map(Account::getName)
                .orElse("-"));
        createCell(row, 15, style, summary.getCreatedAt());

        createCell(row, 16, style, Optional.ofNullable(accounts.get(summary.getUpdatedBy()))
                .map(Account::getName)
                .orElse("-"));
        createCell(row, 17, style, summary.getUpdatedAt());
    }

    private void createCell(XSSFRow row, int colIndex, XSSFCellStyle style, String value) {
        XSSFCell cell = row.createCell(colIndex);
        cell.setCellStyle(style);
        cell.setCellValue(value);
    }

    private void createCell(XSSFRow row, int colIndex, XSSFCellStyle style, Enum<?> value) {
        createCell(row, colIndex, style, value.name());
    }

    private void createCell(XSSFRow row, int colIndex, XSSFCellStyle style, Instant value) {
        if (value == null)
            createEmptyCell(row, colIndex, style);
        else
            createCell(row, colIndex, style, value.atZone(ZONE_LOCAL).format(DATE_TIME_FORMAT));
    }

    private void createCell(XSSFRow row, int colIndex, XSSFCellStyle style, boolean value) {
        createCell(row, colIndex, style, value ? "Y" : "N");
    }

    private void createCell(XSSFRow row, int colIndex, XSSFCellStyle style, Duration value) {
        createCell(row, colIndex, style, Util.durationDescribe(value));
    }

    private void createEmptyCell(XSSFRow row, int colIndex, XSSFCellStyle style) {
        XSSFCell cell = row.createCell(colIndex);
        cell.setCellStyle(style);
        cell.setCellValue((String) null);
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
