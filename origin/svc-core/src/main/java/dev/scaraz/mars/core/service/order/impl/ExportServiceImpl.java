package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.tools.enums.DirectoryAlias;
import dev.scaraz.mars.core.domain.order.AgentWorklog;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.AgentQueryService;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;
import dev.scaraz.mars.core.service.StorageService;
import dev.scaraz.mars.core.service.order.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static dev.scaraz.mars.common.utils.AppConstants.ZONE_LOCAL;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final AgentQueryService agentQueryService;
    private final TicketSummaryQueryService ticketSummaryQueryService;
    private final StorageService storageService;

    @Override
    public File exportToCSV(TicketSummaryCriteria criteria) throws IOException {
        List<TicketSummary> all = ticketSummaryQueryService.findAll(criteria);

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        List<String> rows = new ArrayList<>();
        rows.add(String.join(";", CSV_HEADER));

        for (TicketSummary s : all) {
            AgentWorkspace workspace = agentQueryService.getLastWorkspace(s.getId());
            List<String> row = new ArrayList<>();

            row.add(s.getNo());
            row.add(s.getStatus().name());
            row.add(s.getWitel().name());
            row.add(s.getSto());
            row.add(s.getIncidentNo());
            row.add(s.getServiceNo());
            row.add(s.getSource().name());
            row.add(s.isGaul() ? "Y" : "N");
            row.add(s.getAge().getAge().toString());
            row.add(s.getSenderName());
            row.add(s.getIssue().getName());
            row.add(s.getProduct().name());

            Optional<AgentWorklog> lastWorklog = workspace.getLastWorklog();
            if (lastWorklog.isPresent()) {
                AgentWorklog worklog = lastWorklog.get();
                row.add(worklog.getSolution());
                row.add(worklog.getMessage());
            }
            else {
                row.add(null);
                row.add(null);
            }

            row.add(s.getCreatedBy());
            row.add(s.getCreatedAt().atZone(ZONE_LOCAL).format(format));
            row.add(Objects.requireNonNullElse(s.getUpdatedBy(), "-"));
            row.add(Optional.ofNullable(s.getUpdatedAt())
                    .map(t -> t.atZone(ZONE_LOCAL).format(format))
                    .orElse("-"));

            rows.add(String.join(";", row.stream()
                    .map(v -> v == null ? "" : v)
                    .toArray(String[]::new)));
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
            "Dibuat Oleh",
            "Tgl Dibuat",
            "Diubah Oleh",
            "Tgl Diubah"
    };
}
