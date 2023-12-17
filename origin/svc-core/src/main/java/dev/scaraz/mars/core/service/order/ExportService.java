package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;

import java.io.File;
import java.io.IOException;

public interface ExportService {
    File exportToCSV(TicketSummaryCriteria criteria) throws IOException;
}
