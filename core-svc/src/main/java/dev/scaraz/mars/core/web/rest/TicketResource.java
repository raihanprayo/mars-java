package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.filter.type.BooleanFilter;
import dev.scaraz.mars.common.tools.filter.type.ProductFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.order.*;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.mapper.AgentMapper;
import dev.scaraz.mars.core.query.AgentQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/ticket")
public class TicketResource {

    private final TicketService service;
    private final TicketQueryService queryService;
    private final TicketSummaryQueryService summaryQueryService;

    private final TicketAssetRepo assetRepo;
    private final LogTicketRepo logTicketRepo;

    private final AgentMapper agentMapper;
    private final AgentQueryService agentQueryService;

    @GetMapping
    public ResponseEntity<?> findAll(TicketSummaryCriteria criteria, Pageable pageable) {
//        HttpHeaders headers = new HttpHeaders();
//        if (criteria.getWip() == null)
//            criteria.setWip(new BooleanFilter().setEq(false));
//
        Page<TicketSummary> page = summaryQueryService.findAll(criteria, pageable);
//        attachProductCountHeader(headers, criteria.copy(), false);
//        return ResourceUtil.pagination(page, headers, "/api/ticket");
        return ResourceUtil.pagination(page, "/ticket");
    }

    @GetMapping("/inbox")
    public ResponseEntity<?> getInbox(
            @RequestParam(defaultValue = "false") boolean counter,
            TicketSummaryCriteria criteria,
            Pageable pageable
    ) {
        criteria.setWipBy(new StringFilter().setEq(SecurityUtil.getCurrentUser().getId()));
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

    @GetMapping("/detail/{ticketIdOrNo}")
    public ResponseEntity<?> findById(@PathVariable String ticketIdOrNo) {
        TicketSummary ticket = summaryQueryService.findByIdOrNo(ticketIdOrNo);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    @GetMapping("/detail/{ticketIdOrNo}/relation")
    public ResponseEntity<?> getRelations(@PathVariable String ticketIdOrNo) {
        List<TicketSummary> summaries = summaryQueryService.getGaulRelatedByIdOrNo(ticketIdOrNo);
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/detail/{ticketIdOrNo}/workspaces")
    public ResponseEntity<?> getWorkspaces(
            @RequestParam(defaultValue = "false") boolean full,
            @PathVariable String ticketIdOrNo) {
        List<AgentWorkspace> workspaces = agentQueryService.findWorkspacesByTicket(ticketIdOrNo);
        return ResponseEntity.ok(workspaces.stream()
                .map(o -> full ? agentMapper.toFullDTO(o) : agentMapper.toDTO(o))
                .collect(Collectors.toList()));
    }

    @GetMapping("/detail/{ticketIdOrNo}/worklogs")
    public ResponseEntity<?> getWorklogs(
            @RequestParam(defaultValue = "false") boolean full,
            @PathVariable String ticketIdOrNo) {
        List<AgentWorklog> workspaces = agentQueryService.findWorklogsByTicketIdOrNo(ticketIdOrNo);
        return ResponseEntity.ok(workspaces.stream()
                .map(o -> full ? agentMapper.toFullDTO(o) : agentMapper.toDTO(o))
                .collect(Collectors.toList()));
    }

    @GetMapping("/detail/{ticketIdOrNo}/logs")
    public ResponseEntity<?> getLogs(
            @PathVariable String ticketIdOrNo
    ) {
        List<LogTicket> logs = logTicketRepo.findAllByTicketIdOrTicketNo(ticketIdOrNo, ticketIdOrNo);
        return ResponseEntity.ok(logs);
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

    @GetMapping(path = "/reports", produces = AppConstants.MimeType.APPLICATION_CSV_VALUE)
    public ResponseEntity<?> getReports(TicketCriteria criteria) throws IOException {
        File report = service.report(criteria);
        return ResourceUtil.downloadAndDelete(report);
    }

    private void attachProductCountHeader(HttpHeaders headers, TicketSummaryCriteria criteria, boolean currentUser) {
        if (currentUser) {
            User usr = SecurityUtil.getCurrentUser();
            if (usr != null) {
                StringFilter userIdFilter = new StringFilter(usr.getId());

                long countInternet = summaryQueryService.count(criteria.toBuilder()
                        .product(new ProductFilter().setEq(Product.INTERNET))
                        .wipBy(userIdFilter)
                        .build());
                long countIptv = summaryQueryService.count(criteria.toBuilder()
                        .product(new ProductFilter().setEq(Product.IPTV))
                        .wipBy(userIdFilter)
                        .build());
                long countVoice = summaryQueryService.count(criteria.toBuilder()
                        .product(new ProductFilter().setEq(Product.VOICE))
                        .wipBy(userIdFilter)
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
