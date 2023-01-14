package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.domain.response.TicketPieChartDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.filter.type.InstantFilter;
import dev.scaraz.mars.common.tools.filter.type.ProductFilter;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;
import dev.scaraz.mars.core.service.order.ChartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/chart")
public class ChartResource {

    private final ChartService chartService;
    private final TicketSummaryQueryService summaryQueryService;

    @GetMapping("/ticket/report")
    public ResponseEntity<?> getTicketReports(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        if (from.isAfter(to))
            throw BadRequestException.args("param from cannot be more than to");

        Instant[] inst = chartService.rangeConvert(from, to);

        TicketPieChartDTO chart = new TicketPieChartDTO();
        chart.getCount().setTotal(summaryQueryService.count(TicketSummaryCriteria.builder()
                .createdAt(new InstantFilter()
                        .setGte(inst[0])
                        .setLte(inst[1]))
                .build()));
        chart.getCount().setInternet(count(Product.INTERNET, inst[0], inst[1]));
        chart.getCount().setIptv(count(Product.IPTV, inst[0], inst[1]));
        chart.getCount().setVoice(count(Product.VOICE, inst[0], inst[1]));

        chart.setAge(chartService.pieTicketByAge(from, to));
        chart.setActionAge(chartService.pieTicketByActionAge(from, to));
        chart.setResponseAge(chartService.pieTicketByResponseAge(from, to));

        return ResponseEntity.ok(chart);
    }

    private long count(Product product, Instant from, Instant to) {
        return summaryQueryService.count(TicketSummaryCriteria.builder()
                .product(new ProductFilter().setEq(product))
                .createdAt(new InstantFilter()
                        .setGte(from)
                        .setLte(to))
                .build());
    }
}
