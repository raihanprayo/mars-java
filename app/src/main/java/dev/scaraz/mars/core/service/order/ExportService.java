package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.core.domain.view.TicketSummary;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ExportService {

    File exportTicketsToCSV(List<TicketSummary> all) throws IOException;

    File exportTicketsToExcel(List<TicketSummary> all) throws IOException;
}
