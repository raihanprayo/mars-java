package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.domain.response.LeaderBoardDTO;
import dev.scaraz.mars.common.domain.response.TicketPieChartDTO;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.filter.type.ProductFilter;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.query.criteria.LeaderBoardCriteria;
import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;
import dev.scaraz.mars.core.service.order.ChartService;
import dev.scaraz.mars.core.service.order.ExportService;
import dev.scaraz.mars.core.service.order.LeaderBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/chart")
public class ChartResource {

    private final ChartService chartService;
    private final LeaderBoardService leaderBoardService;
    private final TicketSummaryQueryService ticketSummaryQueryService;

    private final ExportService exportService;

    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderBoard(LeaderBoardCriteria criteria, Pageable pageable) {
        List<LeaderBoardDTO> page = leaderBoardService.getLeaderboard(criteria);
        Optional<Sort.Order> first = pageable.getSort().get().findFirst();

        if (first.isPresent()) {
            page = page.stream().sorted((a, b) -> {
                        Sort.Order order = first.get();
                        String property = order.getProperty();
                        Sort.Direction direction = order.getDirection();
                        switch (property) {
                            case "nik":
                                return direction == Sort.Direction.ASC ?
                                        a.getNik().compareTo(b.getNik()) :
                                        Collections.reverseOrder().compare(a.getNik(), b.getNik());
                            case "name":
                                return direction == Sort.Direction.ASC ?
                                        a.getName().compareTo(b.getName()) :
                                        Collections.reverseOrder().compare(a.getName(), b.getName());
                            case "avgAction":
                                return direction == Sort.Direction.ASC ?
                                        Long.compare(a.getAvgAction(), b.getAvgAction()) :
                                        Long.compare(b.getAvgAction(), a.getAvgAction());
                            case "total":
                                return direction == Sort.Direction.ASC ?
                                        Long.compare(a.getTotal(), b.getTotal()) :
                                        Long.compare(b.getTotal(), a.getTotal());
                            case "totalDispatch":
                                return direction == Sort.Direction.ASC ?
                                        Long.compare(a.getTotalDispatch(), b.getTotalDispatch()) :
                                        Long.compare(b.getTotalDispatch(), a.getTotalDispatch());
                            case "totalHandleDispatch":
                                return direction == Sort.Direction.ASC ?
                                        Long.compare(a.getTotalHandleDispatch(), b.getTotalHandleDispatch()) :
                                        Long.compare(b.getTotalHandleDispatch(), a.getTotalHandleDispatch());
                            case "score": {
                                double a1 = a.getTotal() - (a.getTotalDispatch() * 0.1) + (a.getTotalHandleDispatch() * 0.1);
                                double b1 = b.getTotal() - (b.getTotalDispatch() * 0.1) + (b.getTotalHandleDispatch() * 0.1);
                                return direction == Sort.Direction.ASC ? Double.compare(a1, b1) : Double.compare(b1, a1);
                            }
                        }

                        return 0;
                    })
                    .collect(Collectors.toList());
        }
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("/ticket/report")
    public ResponseEntity<?> getTicketReports(
            TicketSummaryCriteria criteria,
            Pageable pageable
    ) {
        TicketPieChartDTO chart = new TicketPieChartDTO();
        chart.getCount().setTotal(ticketSummaryQueryService.count(criteria));
        chart.getCount().setInternet(count(Product.INTERNET, criteria));
        chart.getCount().setIptv(count(Product.IPTV, criteria));
        chart.getCount().setVoice(count(Product.VOICE, criteria));

        Page<TicketSummary> all = ticketSummaryQueryService.findAll(criteria, pageable);
        chart.setStatus(chartService.pieTicketByStatus(all.getContent()));
        chart.setAge(chartService.pieTicketByAge(all.getContent()));
        chart.setActionAge(chartService.pieTicketByActionAge(all.getContent()));
        chart.setResponseAge(chartService.pieTicketByResponseAge(all.getContent()));

        HttpHeaders headers = ResourceUtil.generatePaginationHeader(all, "/chart/ticket/report");
        return new ResponseEntity<>(Map.of(
                "chart", chart,
                "raw", all.getContent()
        ), headers, HttpStatus.OK);
    }

    @GetMapping("/ticket/report/download")
    public ResponseEntity<?> getTicketReportsDownload(TicketSummaryCriteria criteria) throws IOException {
        File file = exportService.exportToCSV(criteria);
        return ResourceUtil.downloadAndDelete(file);
    }

    private long count(Product product, TicketSummaryCriteria criteria) {
        return ticketSummaryQueryService.count(criteria.toBuilder()
                .product(new ProductFilter().setEq(product))
                .build());
    }

}
