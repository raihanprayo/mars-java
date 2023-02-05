package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.domain.response.UserLeaderboardDTO;
import dev.scaraz.mars.common.tools.filter.type.InstantFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.order.TicketAgent;
import dev.scaraz.mars.core.query.TicketAgentQueryService;
import dev.scaraz.mars.core.query.criteria.LeaderboardCriteria;
import dev.scaraz.mars.core.query.criteria.TicketAgentCriteria;
import dev.scaraz.mars.core.service.order.ChartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/ticket/agent")
public class TicketAgentResource {

    private final TicketAgentQueryService queryService;

    private final ChartService chartService;

    @GetMapping
    public ResponseEntity<?> findAgents(TicketAgentCriteria criteria, Pageable pageable) {
        Page<TicketAgent> page = queryService.findAll(criteria, pageable);
        return ResourceUtil.pagination(page, "/api/ticket/agents");
    }

    @GetMapping("/detail/{ticketIdOrNo}")
    public ResponseEntity<?> detailedAgents(
            @PathVariable String ticketIdOrNo,
            TicketAgentCriteria criteria
    ) {
        try {
            UUID uuid = UUID.fromString(ticketIdOrNo);
            criteria.setTicketId(new StringFilter().setEq(ticketIdOrNo));
        }
        catch (IllegalArgumentException e) {
            criteria.setTicketNo(new StringFilter().setEq(ticketIdOrNo));
        }

        return new ResponseEntity<>(
                queryService.findAll(criteria),
                HttpStatus.OK
        );
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboardStatistic(
            LeaderboardCriteria criteria,
            Pageable pageable
    ) throws ExecutionException, InterruptedException, TimeoutException {
        log.debug("Leaderboard Criteria {}", criteria);
        Page<UserLeaderboardDTO> page = chartService.getLeaderBoard(criteria, pageable)
                .get(5, TimeUnit.MINUTES);
        return ResourceUtil.pagination(page, "/ticket/agent/leaderboard");
    }

}
