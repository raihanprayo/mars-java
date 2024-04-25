package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.domain.response.TicketChartDataCountDTO;
import dev.scaraz.mars.common.domain.response.TicketPieChartDTO;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.filter.type.BooleanFilter;
import dev.scaraz.mars.common.tools.filter.type.ProductFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;
import dev.scaraz.mars.core.service.order.ChartService;
import dev.scaraz.mars.core.service.order.ExportService;
import dev.scaraz.mars.core.service.order.LeaderBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/chart")
public class ChartResource {

    private final ChartService chartService;
    private final LeaderBoardService leaderBoardService;
    private final TicketSummaryQueryService ticketSummaryQueryService;

    private final ExportService exportService;

    @GetMapping("/ticket/report")
    public ResponseEntity<?> getTicketChartReport(TicketSummaryCriteria criteria) {
        criteria.setDeleted(new BooleanFilter().setEq(false));

        TicketPieChartDTO chart = new TicketPieChartDTO();
        chartService.applyPieTicketStats(chart, criteria);

        return ResponseEntity.ok(chart);
    }

    @GetMapping("/ticket/report/count")
    public ResponseEntity<?> getTicketReportCount(TicketSummaryCriteria criteria) {
        criteria.setDeleted(new BooleanFilter().setEq(false));
        TicketChartDataCountDTO count = new TicketChartDataCountDTO();

        StringFilter wipBy = criteria.getWipBy();

        log.debug("Summary Criteria (Total) - {}", criteria);
        count.setTotal(ticketSummaryQueryService.count(criteria));

        count.setInternet(count(Product.INTERNET, criteria));
        count.setIptv(count(Product.IPTV, criteria));
        count.setVoice(count(Product.VOICE, criteria));
        count.setOthers(count(Product.OTHERS, criteria));

        return ResponseEntity.ok(count);
    }

    @GetMapping("/ticket/report/download")
    public ResponseEntity<?> getTicketReportsDownload(TicketSummaryCriteria criteria) throws IOException {
        criteria.setDeleted(new BooleanFilter().setEq(false));
        List<TicketSummary> all = ticketSummaryQueryService.findAll(criteria);
        File file = exportService.exportTicketsToExcel(all);

        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
        String ldt = "report_%s.xlsx".formatted(LocalDateTime.now().format(formatDate));
        return ResourceUtil.downloadAndDelete(file, ldt);
    }

    private long count(Product product, TicketSummaryCriteria criteria) {
        log.debug("Summary Criteria ({}) - {}", product, criteria);
        return ticketSummaryQueryService.count(criteria.copy()
                .setDeleted(new BooleanFilter().setEq(false))
                .setProduct(new ProductFilter().setEq(product)));
    }

}
