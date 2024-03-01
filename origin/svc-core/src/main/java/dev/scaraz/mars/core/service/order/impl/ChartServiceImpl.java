package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.domain.response.PieChartDTO;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.domain.view.WorklogSummary;
import dev.scaraz.mars.core.query.WorklogSummaryQueryService;
import dev.scaraz.mars.core.query.criteria.WorklogSummaryCriteria;
import dev.scaraz.mars.core.repository.db.order.LogTicketRepo;
import dev.scaraz.mars.core.service.order.ChartService;
import dev.scaraz.mars.core.service.order.LogTicketService;
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

    private final LogTicketRepo logTicketRepo;
    private final LogTicketService logTicketService;
    private final WorklogSummaryQueryService worklogSummaryQueryService;

    @Override
    @Transactional(readOnly = true)
    public List<PieChartDTO<String>> pieTicketByAge(List<TicketSummary> summaries) {
        Map<String, PieChartDTO<String>> category = createDurationCategoryMap();
        Instant now = Instant.now();

        for (TicketSummary summary : summaries) {
            switch (summary.getStatus()) {
                case CLOSED:
                    groupAgeAndPush(summary.getCreatedAt(), summary.getUpdatedAt(), category);
                    break;
                default:
                    groupAgeAndPush(now, summary.getCreatedAt(), category);
                    break;
            }
        }

        return new ArrayList<>(category.values());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PieChartDTO<String>> pieTicketByActionAge(List<TicketSummary> summaries, WorklogSummaryCriteria criteria) {
        Map<String, PieChartDTO<String>> category = createDurationCategoryMap();
        Instant now = Instant.now();

        for (TicketSummary summary : summaries) {
            List<WorklogSummary> wls = worklogSummaryQueryService.findAll(
                    criteria.setTicketId(new StringFilter().setEq(summary.getId())));


            for (WorklogSummary wl : wls) {
                switch (wl.getStatus()) {
                    case PROGRESS:
                        groupAgeAndPush(now, wl.getWlCreatedAt(), category);
                        break;
                    default:
                        groupAgeAndPush(wl.getWlUpdatedAt(), wl.getWlCreatedAt(), category);
                        break;
                }
            }
        }

        return new ArrayList<>(category.values());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PieChartDTO<String>> pieTicketByResponseAge(List<TicketSummary> summaries, WorklogSummaryCriteria criteria) {
        Map<String, PieChartDTO<String>> category = createDurationCategoryMap();

        for (TicketSummary summary : summaries) {
            List<WorklogSummary> wls = worklogSummaryQueryService.findAll(
                    criteria.setTicketId(new StringFilter().setEq(summary.getId())));
            for (WorklogSummary wl : wls) {
                switch (wl.getTakeStatus()) {
                    case OPEN:
                        groupAgeAndPush(wl.getWlCreatedAt(), summary.getCreatedAt(), category);
                        break;
                    case DISPATCH:
                        logTicketService.getLogByTicketIdAndBelow(summary.getId(), wl.getWlCreatedAt())
                                .ifPresent(lt -> groupAgeAndPush(wl.getWlCreatedAt(), lt.getCreatedAt(), category));
                        break;
                }
            }
        }


        return new ArrayList<>(category.values());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PieChartDTO<TcStatus>> pieTicketByStatus(List<TicketSummary> summaries) {
        Map<TcStatus, PieChartDTO<TcStatus>> category = createStatusCategoryMap();
        for (TicketSummary summary : summaries) {
            TcStatus status = summary.getStatus();
            PieChartDTO<TcStatus> chart = category.get(status);
            chart.setValue(chart.getValue() + 1);
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

    private Map<String, PieChartDTO<String>> createDurationCategoryMap() {
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

    private Map<TcStatus, PieChartDTO<TcStatus>> createStatusCategoryMap() {
        Map<TcStatus, PieChartDTO<TcStatus>> category = new TreeMap<>();
        for (TcStatus value : TcStatus.values())
            category.put(value, new PieChartDTO<>(value));
        return category;
    }

    private void groupAgeAndPush(Instant lastOrCurrrentTime, @Nullable Instant dataCreatedAt, Map<String, PieChartDTO<String>> category) {
        long durationMili = lastOrCurrrentTime.toEpochMilli() - Optional.ofNullable(dataCreatedAt)
                .orElse(lastOrCurrrentTime)
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
