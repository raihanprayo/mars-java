package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.domain.response.PieChartDTO;
import dev.scaraz.mars.common.domain.response.TicketPieChartDTO;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.domain.view.WorklogSummary;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.query.WorklogSummaryQueryService;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;
import dev.scaraz.mars.core.query.criteria.WorklogSummaryCriteria;
import dev.scaraz.mars.core.repository.db.order.LogTicketRepo;
import dev.scaraz.mars.core.service.order.ChartService;
import dev.scaraz.mars.core.service.order.LogTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
    private final TicketSummaryQueryService ticketSummaryQueryService;

    public List<PieChartDTO<String>> pieTicketByAge(List<TicketSummary> summaries) {
        Map<String, PieChartDTO<String>> category = createDurationCategoryMap();
        Instant now = Instant.now();

        for (TicketSummary summary : summaries) {
            switch (summary.getStatus()) {
                case CLOSED:
                    groupAgeAndPush(summary.getClosedAt(), summary.getCreatedAt(), category);
                    break;
                default:
                    groupAgeAndPush(now, summary.getCreatedAt(), category);
                    break;
            }
        }

        return new ArrayList<>(category.values());
    }

    public List<PieChartDTO<String>> pieTicketByActionAge(List<TicketSummary> summaries,
                                                          WorklogSummaryCriteria criteria) {
        Map<String, PieChartDTO<String>> category = createDurationCategoryMap();
        Instant now = Instant.now();

        for (TicketSummary summary : summaries) {
            List<WorklogSummary> wls = worklogSummaryQueryService.findAll(criteria.setTicket(
                    new TicketCriteria()
                            .setId(new StringFilter().setEq(summary.getId()))
            ));


            for (WorklogSummary wl : wls) {
                switch (wl.getStatus()) {
                    case PROGRESS:
                        log.trace("Calculate status in progress - {}", wl);
                        groupAgeAndPush(now, wl.getCreatedAt(), category);
                        break;
                    default:
                        log.trace("Calculate status closed - {}", wl);
                        groupAgeAndPush(Objects.requireNonNullElse(wl.getUpdatedAt(), Instant.now()), wl.getCreatedAt(), category);
                        break;
                }
            }
        }

        return new ArrayList<>(category.values());
    }

    public List<PieChartDTO<String>> pieTicketByResponseAge(List<TicketSummary> summaries,
                                                            WorklogSummaryCriteria criteria) {
        Map<String, PieChartDTO<String>> category = createDurationCategoryMap();

        for (TicketSummary summary : summaries) {
            List<WorklogSummary> wls = worklogSummaryQueryService.findAll(criteria.setTicket(
                    new TicketCriteria()
                            .setId(new StringFilter().setEq(summary.getId()))
            ));
            for (WorklogSummary wl : wls) {
                switch (wl.getTakeStatus()) {
                    case OPEN:
                        groupAgeAndPush(wl.getCreatedAt(), summary.getCreatedAt(), category);
                        break;
                    case DISPATCH:
                        logTicketService.getLogByTicketIdAndBelow(summary.getId(), wl.getCreatedAt())
                                .ifPresent(lt -> groupAgeAndPush(wl.getCreatedAt(), lt.getCreatedAt(), category));
                        break;
                }
            }
        }


        return new ArrayList<>(category.values());
    }

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
    @Transactional(readOnly = true)
    public void applyPieTicketStats(TicketPieChartDTO chart, TicketSummaryCriteria criteria) {
        WorklogSummaryCriteria worklogSummaryCriteria = new WorklogSummaryCriteria();
        if (criteria.getWorkspace() != null)
            worklogSummaryCriteria.setUserId(criteria.getWorkspace().getUserId());

        log.trace("With workspace.userId ? {}", worklogSummaryCriteria.getUserId() != null);

        List<TicketSummary> summaries = ticketSummaryQueryService.findAll(criteria);
        chart.setStatus(pieTicketByStatus(summaries));
        chart.setAge(pieTicketByAge(summaries));
        chart.setActionAge(pieTicketByActionAge(summaries, worklogSummaryCriteria));
        chart.setResponseAge(pieTicketByResponseAge(summaries, worklogSummaryCriteria));
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

    private void groupAgeAndPush(Instant lastOrCurrrentTime, Instant dataCreatedAt, Map<String, PieChartDTO<String>> category) {
        Assert.notNull(lastOrCurrrentTime, "lastOrCurrrentTime cannot be null");
        Assert.notNull(dataCreatedAt, "dataCreatedAt cannot be null");
        long durationMili = lastOrCurrrentTime.toEpochMilli() - dataCreatedAt.toEpochMilli();

        if (durationMili <= 900_000) {
            category.computeIfPresent(CATEGORY_15_MINUTES, (k, v) -> {
                v.setValue(v.getValue() + 1);
                return v;
            });
        }
        else if (durationMili <= 1_800_000) {
            category.computeIfPresent(CATEGORY_30_MINUTES, (k, v) -> {
                v.setValue(v.getValue() + 1);
                return v;
            });
        }
        else if (durationMili <= 3_600_000) {
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
