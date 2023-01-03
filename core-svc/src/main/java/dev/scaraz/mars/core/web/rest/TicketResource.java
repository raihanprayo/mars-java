package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.AgStatusFilter;
import dev.scaraz.mars.common.tools.filter.type.BooleanFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.tools.filter.type.TcStatusFilter;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.order.TicketAgent;
import dev.scaraz.mars.core.domain.order.TicketAsset;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.TicketAgentQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.query.criteria.TicketAgentCriteria;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.repository.order.TicketAssetRepo;
import dev.scaraz.mars.core.service.order.TicketService;
import dev.scaraz.mars.core.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/api/ticket")
public class TicketResource {

    private final TicketService service;

    private final TicketQueryService queryService;

    private final TicketAgentQueryService agentQueryService;
    private final TicketSummaryQueryService summaryQueryService;

    private final TicketAssetRepo assetRepo;

    @GetMapping
    public ResponseEntity<?> findAll(TicketSummaryCriteria criteria, Pageable pageable) {
        HttpHeaders headers = new HttpHeaders();
        Page<TicketSummary> page = summaryQueryService.findAll(criteria, pageable);

        headers.add("TC-COUNT", String.valueOf(
                summaryQueryService.countByProduct(Product.INTERNET, false)
        ));
        headers.add("TC-COUNT", String.valueOf(
                summaryQueryService.countByProduct(Product.IPTV, false)
        ));
        headers.add("TC-COUNT", String.valueOf(
                summaryQueryService.countByProduct(Product.VOICE, false)
        ));

        criteria.setWip(new BooleanFilter().setEq(false));
        return ResourceUtil.pagination(page, headers, "/api/ticket");
    }

    @GetMapping("/inbox")
    public ResponseEntity<?> getInbox(
            @RequestParam(defaultValue = "false") boolean counter,
            TicketSummaryCriteria criteria,
            Pageable pageable
    ) {
        UserCriteria userCriteria = criteria.getWipBy();
        if (userCriteria == null) criteria.setWipBy(userCriteria = new UserCriteria());

        userCriteria.setId(new StringFilter().setEq(SecurityUtil.getCurrentUser().getId()));

        if (counter) {
            long count = summaryQueryService.count(criteria);
            return ResponseEntity.ok(
                    Map.of("total", count)
            );
        }
        else {
            HttpHeaders headers = new HttpHeaders();
            Page<TicketSummary> page = summaryQueryService.findAll(criteria, pageable);

            headers.add("TC-COUNT", String.valueOf(
                    summaryQueryService.countByProduct(Product.INTERNET, true)
            ));
            headers.add("TC-COUNT", String.valueOf(
                    summaryQueryService.countByProduct(Product.IPTV, true)
            ));
            headers.add("TC-COUNT", String.valueOf(
                    summaryQueryService.countByProduct(Product.VOICE, true)
            ));

            return ResourceUtil.pagination(page, headers, "/api/ticket/inbox");
        }
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<?> findById(@PathVariable String ticketId) {
        Ticket ticket = queryService.findById(ticketId);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    @GetMapping("/agents")
    public ResponseEntity<?> findAgents(TicketAgentCriteria criteria, Pageable pageable) {
        Page<TicketAgent> page = agentQueryService.findAll(criteria, pageable);
        return ResourceUtil.pagination(page, "/api/ticket/agents");
    }

    @GetMapping("/agents/{ticketId}")
    public ResponseEntity<?> findAgents(@PathVariable String ticketId, TicketAgentCriteria criteria) {
        criteria.setTicketId(new StringFilter().setEq(ticketId));

        return new ResponseEntity<>(
                agentQueryService.findAll(criteria),
                HttpStatus.OK
        );
    }

    @GetMapping("/assets/{ticketNo}")
    public ResponseEntity<?> getAssets(
            @PathVariable String ticketNo,
            @RequestParam(defaultValue = "false") boolean includeAgent,
            @RequestParam(required = false) String agentId
    ) {
        Ticket ticket = queryService.findByIdOrNo(ticketNo);
        List<TicketAsset> assets;
        if (agentId != null) assets = assetRepo.findAllByTicketIdAndAgentId(ticket.getId(), agentId);
        else {
            if (includeAgent) assets = assetRepo.findAllByTicketId(ticket.getId());
            else assets = assetRepo.findAllByTicketIdAndAgentIsNull(ticket.getId());
        }

        return new ResponseEntity<>(
                assets.stream().flatMap(e -> Stream.of(e.getPaths()))
                        .collect(Collectors.toSet()),
                HttpStatus.OK
        );
    }

    @PostMapping("/take/{ticketId}")
    public ResponseEntity<?> takeTicket(
            @PathVariable String ticketId
    ) {
        Ticket ticket = queryService.findByIdOrNo(ticketId);

        if (agentQueryService.hasAgentInProgressByTicketNo(ticketId))
            throw BadRequestException.args("error.ticket.taken");

        return new ResponseEntity<>(
                service.take(ticket),
                HttpStatus.OK
        );
    }

    private void attachProductCountHeader(HttpHeaders headers, @Nullable TicketAgentCriteria agentCriteria) {
        Map<Product, Long> countProducts = queryService.countProducts(agentCriteria);
        headers.set("TC-INTERNET", String.valueOf(countProducts.get(Product.INTERNET)));
        headers.set("TC-VOICE", String.valueOf(countProducts.get(Product.VOICE)));
        headers.set("TC-IPTV", String.valueOf(countProducts.get(Product.IPTV)));
    }

}
