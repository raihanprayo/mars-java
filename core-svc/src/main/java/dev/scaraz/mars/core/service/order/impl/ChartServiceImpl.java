package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.domain.response.PieChartDTO;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.InstantFilter;
import dev.scaraz.mars.core.domain.order.LogTicket;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;
import dev.scaraz.mars.core.repository.order.LogTicketRepo;
import dev.scaraz.mars.core.service.order.ChartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@RequiredArgsConstructor

@Service
public class ChartServiceImpl implements ChartService {


    private final LogTicketRepo logTicketRepo;
    private final TicketSummaryQueryService ticketSummaryQueryService;


    @Override
    @Transactional(readOnly = true)
    public List<PieChartDTO<String>> pieTicketByActionAge(LocalDate from, LocalDate to) {
        Instant[] instants = rangeConvert(from, to);
        Instant start = instants[0];
        Instant end = instants[1];

        Map<String, LogTicket> temp = new HashMap<>();
        List<LogTicket> logs = logTicketRepo.findAllByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqualOrderByCreatedAtAsc(start, end);

        for (LogTicket log : logs) temp.putIfAbsent(log.getTicket().getId(), log);

        Map<String, PieChartDTO<String>> category = createCategoryMap();
        Instant now = Instant.now();

        for (LogTicket log : temp.values())
            groupAndPush(now, log.getCreatedAt(), category);

        return new ArrayList<>(category.values());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PieChartDTO<String>> pieTicketByAge(LocalDate from, LocalDate to) {
        Instant[] instants = rangeConvert(from, to);
        Instant start = instants[0];
        Instant end = instants[1];

        List<TicketSummary> all = ticketSummaryQueryService.findAll(
                TicketSummaryCriteria.builder()
                        .createdAt(new InstantFilter()
                                .setGte(start)
                                .setLte(end))
                        .build()
        );

        Map<String, PieChartDTO<String>> category = createCategoryMap();
        Instant now = Instant.now();

        for (TicketSummary summary : all)
            groupAndPush(now, summary.getCreatedAt(), category);

        return new ArrayList<>(category.values());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PieChartDTO<String>> pieTicketByResponseAge(LocalDate from, LocalDate to) {
        Instant[] instants = rangeConvert(from, to);
        Instant start = instants[0];
        Instant end = instants[1];

        List<LogTicket> logs = logTicketRepo.findAllByPrevAndCurrAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
                TcStatus.OPEN, TcStatus.PROGRESS,
                start, end);

        Map<String, PieChartDTO<String>> category = createCategoryMap();
        Instant now = Instant.now();

        for (LogTicket log : logs)
            groupAndPush(now, log.getCreatedAt(), category);

        return new ArrayList<>(category.values());
    }

    @Override
    public Instant[] rangeConvert(LocalDate from, LocalDate to) {
        Instant startInst = from.atStartOfDay().toInstant(ZoneOffset.of("+07"));
        Instant endInst = to.atStartOfDay()
                .plusDays(1)
                .minusSeconds(1)
                .toInstant(ZoneOffset.of("+07"));
        return new Instant[]{startInst, endInst};
    }

    private Map<String, PieChartDTO<String>> createCategoryMap() {
        Map<String, PieChartDTO<String>> category = new TreeMap<>();
        category.put(CATEGORY_15_MINUTES, PieChartDTO.<String>builder()
                .type(CATEGORY_15_MINUTES)
                .value(0)
                .color("blue")
                .build());

        category.put(CATEGORY_30_MINUTES, PieChartDTO.<String>builder()
                .type(CATEGORY_30_MINUTES)
                .value(0)
                .color("orange")
                .build());

        category.put(CATEGORY_60_MINUTES, PieChartDTO.<String>builder()
                .type(CATEGORY_60_MINUTES)
                .value(0)
                .color("purple")
                .build());

        category.put(CATEGORY_MORE_THAN_60_MINUTES, PieChartDTO.<String>builder()
                .type(CATEGORY_MORE_THAN_60_MINUTES)
                .value(0)
                .color("red")
                .build());
        return category;
    }

    private void groupAndPush(Instant now, Instant dataCreatedAt, Map<String, PieChartDTO<String>> category) {
        long durationMili = now.toEpochMilli() - dataCreatedAt.toEpochMilli();
        if (durationMili <= MILI_15_MINUTES.toMillis()) {
            category.computeIfPresent(CATEGORY_15_MINUTES, (k, v) -> {
                v.setValue(v.getValue() + 1);
                return v;
            });
        }
        else if (durationMili <= MILI_30_MINUTES.toMillis()) {
            category.computeIfPresent(CATEGORY_30_MINUTES, (k, v) -> {
                v.setValue(v.getValue() + 1);
                return v;
            });
        }
        else if (durationMili <= MILI_60_MINUTES.toMillis()) {
            category.computeIfPresent(CATEGORY_60_MINUTES, (k, v) -> {
                v.setValue(v.getValue() + 1);
                return v;
            });
        }
        else {
            category.computeIfPresent(CATEGORY_MORE_THAN_60_MINUTES, (k, v) -> {
                v.setValue(v.getValue() + 1);
                return v;
            });
        }
    }

}
