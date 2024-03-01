package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.tools.enums.DirectoryAlias;
import dev.scaraz.mars.common.utils.Util;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.order.AgentWorklog;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.AgentQueryService;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.service.StorageService;
import dev.scaraz.mars.core.service.order.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static dev.scaraz.mars.common.utils.AppConstants.ZONE_LOCAL;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final AgentQueryService agentQueryService;
    private final TicketSummaryQueryService ticketSummaryQueryService;
    private final StorageService storageService;

    private final AccountQueryService accountQueryService;

    @Override
    @Transactional(readOnly = true)
    public File exportToCSV(List<TicketSummary> all) throws IOException {
//        List<TicketSummary> all = ticketSummaryQueryService.findAll(criteria);

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        List<String> rows = new ArrayList<>();
        rows.add(String.join(";", CSV_HEADER));

        Map<String, Account> accounts = accountQueryService.findAll().stream()
                .collect(Collectors.toMap(Account::getNik, a -> a));

        for (TicketSummary s : all) {
            log.debug("Export {} / {}", s.getId(), s.getWorkspaces());
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
                row.add(Util.durationDescribe(s.getAge().getAge()));
                row.add(s.getSenderName());
                row.add(s.getIssue().getName());
                row.add(s.getProduct().name());

                Optional<AgentWorkspace> workspaceOpt = agentQueryService.getLastWorkspaceOptional(s.getId());
                if (workspaceOpt.isPresent()) {
                    Optional<AgentWorklog> lastWorklog = workspaceOpt.get().getLastWorklog();
                    if (lastWorklog.isPresent()) {
                        AgentWorklog worklog = lastWorklog.get();
                        row.add(worklog.getSolution());
                        row.add(worklog.getMessage());
                    }
                    else {
                        row.add(null);
                        row.add(null);
                    }
                }
                else {
                    row.add(null);
                    row.add(null);
                }

                row.add(Optional.ofNullable(s.getCreatedBy())
                        .map(accounts::get)
                        .map(Account::getName)
                        .orElse("-"));
                row.add(s.getCreatedAt().atZone(ZONE_LOCAL).format(format));

                row.add(Optional.ofNullable(s.getUpdatedBy())
                        .map(accounts::get)
                        .map(Account::getName)
                        .orElse("-"));
                row.add(Optional.ofNullable(s.getUpdatedAt())
                        .map(t -> t.atZone(ZONE_LOCAL).format(format))
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
