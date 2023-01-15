package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.filter.type.BooleanFilter;
import dev.scaraz.mars.common.tools.filter.type.ProductFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.order.LogTicket;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.order.TicketAgent;
import dev.scaraz.mars.core.domain.order.TicketAsset;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.TicketAgentQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.query.criteria.TicketAgentCriteria;
import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.repository.order.LogTicketRepo;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/ticket")
public class TicketResource {

    private final TicketService service;

    private final TicketQueryService queryService;

    private final TicketAgentQueryService agentQueryService;
    private final TicketSummaryQueryService summaryQueryService;

    private final TicketAssetRepo assetRepo;
    private final LogTicketRepo logTicketRepo;

    @GetMapping
    public ResponseEntity<?> findAll(TicketSummaryCriteria criteria, Pageable pageable) {
        HttpHeaders headers = new HttpHeaders();
        if (criteria.getWip() == null)
            criteria.setWip(new BooleanFilter().setEq(false));

        Page<TicketSummary> page = summaryQueryService.findAll(criteria, pageable);
        attachProductCountHeader(headers, criteria.copy(), false);
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
            attachProductCountHeader(headers, criteria.copy(), true);

            Page<TicketSummary> page = summaryQueryService.findAll(criteria, pageable);
            return ResourceUtil.pagination(page, headers, "/api/ticket/inbox");
        }
    }

    @GetMapping("/{ticketIdOrNo}")
    public ResponseEntity<?> findById(@PathVariable String ticketIdOrNo) {
        TicketSummary ticket = summaryQueryService.findByIdOrNo(ticketIdOrNo);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    @GetMapping("/{ticketIdOrNo}/relation")
    public ResponseEntity<?> getRelations(@PathVariable String ticketIdOrNo) {
        List<TicketSummary> summaries = summaryQueryService.getGaulRelatedByIdOrNo(ticketIdOrNo);
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/agents")
    public ResponseEntity<?> findAgents(TicketAgentCriteria criteria, Pageable pageable) {
        Page<TicketAgent> page = agentQueryService.findAll(criteria, pageable);
        return ResourceUtil.pagination(page, "/api/ticket/agents");
    }

    @GetMapping("/agents/{ticketIdOrNo}")
    public ResponseEntity<?> findAgents(@PathVariable String ticketIdOrNo, TicketAgentCriteria criteria) {
        try {
            UUID uuid = UUID.fromString(ticketIdOrNo);
            criteria.setTicketId(new StringFilter().setEq(ticketIdOrNo));
        }
        catch (IllegalArgumentException e) {
            criteria.setTicketNo(new StringFilter().setEq(ticketIdOrNo));
        }

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

    @GetMapping("/logs/{ticketIdOrNo}")
    public ResponseEntity<?> getLogs(
            @PathVariable String ticketIdOrNo
    ) {
        List<LogTicket> logs = logTicketRepo.findAllByTicketIdOrTicketNo(ticketIdOrNo, ticketIdOrNo);
        return ResponseEntity.ok(logs);
    }

    private void attachProductCountHeader(HttpHeaders headers, TicketSummaryCriteria criteria, boolean currentUser) {
        if (currentUser) {
            User usr = SecurityUtil.getCurrentUser();
            if (usr != null) {
                UserCriteria userCriteria = UserCriteria.builder()
                        .id(new StringFilter(usr.getId()))
                        .build();

                long countInternet = summaryQueryService.count(criteria.toBuilder()
                        .product(new ProductFilter().setEq(Product.INTERNET))
                        .wipBy(userCriteria)
                        .build());
                long countIptv = summaryQueryService.count(criteria.toBuilder()
                        .product(new ProductFilter().setEq(Product.IPTV))
                        .wipBy(userCriteria)
                        .build());
                long countVoice = summaryQueryService.count(criteria.toBuilder()
                        .product(new ProductFilter().setEq(Product.VOICE))
                        .wipBy(userCriteria)
                        .build());

                headers.add("Tc-Count", String.valueOf(countInternet));
                headers.add("Tc-Count", String.valueOf(countIptv));
                headers.add("Tc-Count", String.valueOf(countVoice));
            }
        }
        else {
            long countInternet = summaryQueryService.count(criteria.toBuilder()
                    .product(new ProductFilter().setEq(Product.INTERNET))
                    .build());
            long countIptv = summaryQueryService.count(criteria.toBuilder()
                    .product(new ProductFilter().setEq(Product.IPTV))
                    .build());
            long countVoice = summaryQueryService.count(criteria.toBuilder()
                    .product(new ProductFilter().setEq(Product.VOICE))
                    .build());

            headers.add("Tc-Count", String.valueOf(countInternet));
            headers.add("Tc-Count", String.valueOf(countIptv));
            headers.add("Tc-Count", String.valueOf(countVoice));
        }
    }

}
