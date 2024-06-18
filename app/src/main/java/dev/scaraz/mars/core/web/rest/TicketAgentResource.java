package dev.scaraz.mars.core.web.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/ticket/agent")
public class TicketAgentResource {

//    private final AgentQueryService queryService;
//
//    private final ChartService chartService;
//
//    @GetMapping
//    public ResponseEntity<?> findAgents(AgentCriteria criteria, Pageable pageable) {
//        Page<Agent> page = queryService.findAll(criteria, pageable);
//        return ResourceUtil.pagination(page, "/api/ticket/agents");
//    }

}
