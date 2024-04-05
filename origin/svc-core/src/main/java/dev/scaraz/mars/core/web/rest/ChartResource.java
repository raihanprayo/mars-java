package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.domain.response.TicketPieChartDTO;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.filter.type.BooleanFilter;
import dev.scaraz.mars.common.tools.filter.type.ProductFilter;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;
import dev.scaraz.mars.core.query.criteria.WorklogSummaryCriteria;
import dev.scaraz.mars.core.service.order.ChartService;
import dev.scaraz.mars.core.service.order.ExportService;
import dev.scaraz.mars.core.service.order.LeaderBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> getTicketReports(
            TicketSummaryCriteria criteria,
            Pageable pageable
    ) {
        criteria.setDeleted(new BooleanFilter().setEq(false));
        TicketPieChartDTO chart = new TicketPieChartDTO();
        chart.getCount().setTotal(ticketSummaryQueryService.count(criteria));
        chart.getCount().setInternet(count(Product.INTERNET, criteria));
        chart.getCount().setIptv(count(Product.IPTV, criteria));
        chart.getCount().setVoice(count(Product.VOICE, criteria));


        WorklogSummaryCriteria worklogSummaryCriteria = new WorklogSummaryCriteria();
        if (criteria.getWorkspace() != null)
            worklogSummaryCriteria.setUserId(criteria.getWorkspace().getUserId());

        log.trace("With workspace.userId ? {}", worklogSummaryCriteria.getUserId() != null);

        Page<TicketSummary> all = ticketSummaryQueryService.findAll(criteria, pageable);
        chart.setStatus(chartService.pieTicketByStatus(all.getContent()));

        chart.setAge(chartService.pieTicketByAge(all.getContent()));

        log.info("By Action Age");
        chart.setActionAge(chartService.pieTicketByActionAge(
                all.getContent(),
                worklogSummaryCriteria));

        log.info("By Response Age");
        chart.setResponseAge(chartService.pieTicketByResponseAge(
                all.getContent(),
                worklogSummaryCriteria));

        HttpHeaders headers = ResourceUtil.generatePaginationHeader(all, "/chart/ticket/report");
        return new ResponseEntity<>(Map.of(
                "chart", chart,
                "raw", all.getContent()
        ), headers, HttpStatus.OK);
    }

    @GetMapping("/ticket/report/download")
    public ResponseEntity<?> getTicketReportsDownload(TicketSummaryCriteria criteria) throws IOException {
        List<TicketSummary> all = ticketSummaryQueryService.findAll(criteria);
        File file = exportService.exportToCSV(all);
        return ResourceUtil.downloadAndDelete(file);
    }

    private long count(Product product, TicketSummaryCriteria criteria) {
        return ticketSummaryQueryService.count(criteria.copy()
                .setProduct(new ProductFilter().setEq(product)));
    }

}
