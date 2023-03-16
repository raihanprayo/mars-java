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
import dev.scaraz.mars.core.service.order.LeaderBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/chart")
public class ChartResource {

    private final ChartService chartService;
    private final LeaderBoardService leaderBoardService;
    private final TicketSummaryQueryService summaryQueryService;

    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderBoard(
            LeaderBoardCriteria criteria,
            Pageable pageable
    ) {
        Page<LeaderBoardDTO> page = leaderBoardService.findAll(criteria, pageable);
        return ResourceUtil.pagination(page, "/chart/leaderboard");
    }

    @GetMapping("/leaderboard/closed")
    public ResponseEntity<?> getClosedTicketLeaderboard(
            LeaderBoardCriteria criteria,
            Pageable pageable
    ) {
        return null;
    }

    @GetMapping("/ticket/report")
    public ResponseEntity<?> getTicketReports(
            TicketSummaryCriteria criteria
    ) {
        TicketPieChartDTO chart = new TicketPieChartDTO();
        chart.getCount().setTotal(summaryQueryService.count(criteria));
        chart.getCount().setInternet(count(Product.INTERNET, criteria));
        chart.getCount().setIptv(count(Product.IPTV, criteria));
        chart.getCount().setVoice(count(Product.VOICE, criteria));

        List<TicketSummary> all = summaryQueryService.findAll(criteria);
        chart.setAge(chartService.pieTicketByAge(all));
        chart.setActionAge(chartService.pieTicketByActionAge(all));
        chart.setResponseAge(chartService.pieTicketByResponseAge(all));

        return ResponseEntity.ok(chart);
    }

    private long count(Product product, TicketSummaryCriteria criteria) {
        return summaryQueryService.count(criteria.toBuilder()
                .product(new ProductFilter().setEq(product))
                .build());
    }
}
