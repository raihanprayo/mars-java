package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.response.PieChartDTO;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface ChartService {
    Duration MILI_15_MINUTES = Duration.ofMinutes(15),
            MILI_30_MINUTES = Duration.ofMinutes(30),
            MILI_60_MINUTES = Duration.ofMinutes(60);

    String CATEGORY_15_MINUTES = "0-15 menit",
            CATEGORY_30_MINUTES = "15-30 menit",
            CATEGORY_60_MINUTES = "30-60 menit",
            CATEGORY_MORE_THAN_60_MINUTES = "60+ menit";

    List<PieChartDTO<String>> pieTicketByActionAge(List<TicketSummary> summaries);

    List<PieChartDTO<String>> pieTicketByAge(List<TicketSummary> summaries);

    List<PieChartDTO<String>> pieTicketByResponseAge(List<TicketSummary> summaries);

    Instant[] rangeConvert(LocalDate from, LocalDate to);
}
