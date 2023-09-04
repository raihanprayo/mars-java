package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.domain.response.PieChartDTO;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.service.order.ChartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@RequiredArgsConstructor

@Service
public class ChartServiceImpl implements ChartService {

    @Override
    @Transactional(readOnly = true)
    public List<PieChartDTO<String>> pieTicketByAge(List<TicketSummary> summaries) {
        Map<String, PieChartDTO<String>> category = createCategoryMap();
        Instant now = Instant.now();

        for (TicketSummary summary : summaries)
            groupAndPush(now, summary.getCreatedAt(), category);

        return new ArrayList<>(category.values());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PieChartDTO<String>> pieTicketByActionAge(List<TicketSummary> summaries) {
        Map<String, PieChartDTO<String>> category = createCategoryMap();
        Instant now = Instant.now();

        for (TicketSummary summary : summaries) {
            groupAndPush(now, summary.getAge().getAction(), category);
        }

        return new ArrayList<>(category.values());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PieChartDTO<String>> pieTicketByResponseAge(List<TicketSummary> summaries) {
        Map<String, PieChartDTO<String>> category = createCategoryMap();
        for (TicketSummary summary : summaries) {
            groupAndPush(summary.getCreatedAt(), summary.getAge().getResponse(), category);
        }

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

    private void groupAndPush(Instant now, @Nullable Instant dataCreatedAt, Map<String, PieChartDTO<String>> category) {
        long durationMili = now.toEpochMilli() - Optional.ofNullable(dataCreatedAt)
                .orElse(now)
                .toEpochMilli();

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
