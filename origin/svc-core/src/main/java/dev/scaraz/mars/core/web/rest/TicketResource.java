package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.domain.response.UserContactDTO;
import dev.scaraz.mars.common.domain.response.UserTgDTO;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.BooleanFilter;
import dev.scaraz.mars.common.tools.filter.type.ProductFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.tools.filter.type.TcStatusFilter;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.common.utils.AuthorityConstant;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.order.*;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.mapper.AgentMapper;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.AgentQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;
import dev.scaraz.mars.core.repository.db.order.LogTicketRepo;
import dev.scaraz.mars.core.repository.db.order.TicketAssetRepo;
import dev.scaraz.mars.core.service.order.TicketService;
import dev.scaraz.mars.core.service.order.flow.CloseFlowService;
import dev.scaraz.mars.core.service.order.flow.DispatchFlowService;
import dev.scaraz.mars.core.service.order.flow.PendingFlowService;
import dev.scaraz.mars.security.MarsUserContext;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/ticket")
public class TicketResource {

    private final AccountQueryService accountQueryService;

    private final TicketService service;
    private final TicketQueryService queryService;
    private final TicketSummaryQueryService summaryQueryService;

    private final TicketAssetRepo assetRepo;
    private final LogTicketRepo logTicketRepo;

    private final AgentMapper agentMapper;
    private final AgentQueryService agentQueryService;

    private final CloseFlowService closeFlowService;
    private final PendingFlowService pendingFlowService;
    private final DispatchFlowService dispatchFlowService;

    @GetMapping
    @Timed
    public ResponseEntity<?> findAll(TicketSummaryCriteria criteria, Pageable pageable) {
        Page<TicketSummary> page = summaryQueryService.findAll(criteria, pageable);
        return ResourceUtil.pagination(page, "/ticket");
    }

    @GetMapping("/inbox")
    @Timed
    public ResponseEntity<?> getInbox(
            @RequestParam(defaultValue = "false") boolean counter,
            TicketSummaryCriteria criteria,
            Pageable pageable
    ) {
        criteria.setDeleted(new BooleanFilter().setEq(false));
        criteria.setWipBy(new StringFilter().setEq(MarsUserContext.getId()));
        if (counter) {
            long count = summaryQueryService.count(criteria);
            return ResponseEntity.ok(
                    Map.of("total", count)
            );
        }
        else {
            HttpHeaders headers = new HttpHeaders();
            attachProductCountHeader(headers, criteria.copy(), true);

            if (criteria.getStatus() == null) {
                criteria.setStatus(new TcStatusFilter()
                        .setIn(List.of(TcStatus.CLOSED))
                        .setNegated(true)
                );
            }

            Page<TicketSummary> page = summaryQueryService.findAll(criteria, pageable);
            return ResourceUtil.pagination(page, headers, "/api/ticket/inbox");
        }
    }

    @DeleteMapping("/remove")
    @Timed
    @PreAuthorize(AuthorityConstant.HAS_ROLE_ADMIN)
    public ResponseEntity<?> deleteTicketBulk(
            @RequestParam(defaultValue = "false") boolean forever,
            @RequestBody List<String> ticketIds
    ) {
        if (!forever)
            service.markDeleted(ticketIds.toArray(String[]::new));
        else
            service.delete(ticketIds.toArray(String[]::new));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/remove/range")
    @Timed
    @PreAuthorize(AuthorityConstant.HAS_ROLE_ADMIN)
    public ResponseEntity<?> deleteTicketByDate(@RequestParam Instant from,
                                                @RequestParam Instant to) {
        long totalDeleted = service.markDeleted(from, to);
        return new ResponseEntity<>(Map.of("total", totalDeleted), HttpStatus.OK);
    }

    @PutMapping("/force/close")
    public ResponseEntity<?> forceCloseTicket(@RequestBody List<String> no) {
        for (String s : no) {
            try {
                closeFlowService.forceClose(s);
            }
            catch (Exception ex) {
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/restore")
    @Timed
    @PreAuthorize(AuthorityConstant.HAS_ROLE_ADMIN)
    public ResponseEntity<?> restoreTicketBulk(@RequestBody List<String> ticketIds) {
        service.restore(ticketIds.toArray(String[]::new));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/resend/pending")
    public void fixPendingTickets() {
        service.resendPending();
    }


//    @GetMapping("/detail/{ticketIdOrNo}")
//    public ResponseEntity<?> getDetails(@PathVariable String ticketIdOrNo) {
//        Map<String, Object> result = new HashMap<>();
//
//        TicketSummary ticket = summaryQueryService.findByIdOrNo(ticketIdOrNo);
//        result.put("ticket", ticket);
//
//        Account account = accountQueryService.findByTelegramId(ticket.getSenderId());
//        UserContactDTO contact = UserContactDTO.builder()
//                .nik(account.getNik())
//                .name(account.getName())
//                .phone(account.getPhone())
//                .tg(UserTgDTO.builder()
//                        .id(account.getTg().getId())
//                        .username(account.getTg().getUsername())
//                        .build())
//                .build();
//        result.put("contact", contact);
//
//        result.put("relation", summaryQueryService.getGaulRelatedByIdOrNo(ticketIdOrNo));
//
//        return null;
//    }

    @GetMapping("/detail/{ticketIdOrNo}")
    public ResponseEntity<?> findById(@PathVariable String ticketIdOrNo) {
        TicketSummary ticket = summaryQueryService.findByIdOrNo(ticketIdOrNo);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    @GetMapping("/detail/{ticketIdOrNo}/contact")
    public ResponseEntity<?> getContact(@PathVariable String ticketIdOrNo) {
        TicketSummary ticket = summaryQueryService.findByIdOrNo(ticketIdOrNo);
        long telegramId = ticket.getSenderId();
        Account account = accountQueryService.findByTelegramId(telegramId);
        UserContactDTO contact = UserContactDTO.builder()
                .nik(account.getNik())
                .name(account.getName())
                .phone(account.getPhone())
                .tg(UserTgDTO.builder()
                        .id(account.getTg().getId())
                        .username(account.getTg().getUsername())
                        .build())
                .build();

        return ResponseEntity.ok(contact);
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
            Account usr = accountQueryService.findByCurrentAccess();
            if (usr != null) {
                StringFilter userIdFilter = new StringFilter(usr.getId());

                long countInternet = summaryQueryService.count(criteria
                        .setProduct(new ProductFilter().setEq(Product.INTERNET))
                        .setWipBy(userIdFilter));
                long countIptv = summaryQueryService.count(criteria
                        .setProduct(new ProductFilter().setEq(Product.IPTV))
                        .setWipBy(userIdFilter));
                long countVoice = summaryQueryService.count(criteria
                        .setProduct(new ProductFilter().setEq(Product.VOICE))
                        .setWipBy(userIdFilter));

                headers.add("Tc-Count", String.valueOf(countInternet));
                headers.add("Tc-Count", String.valueOf(countIptv));
                headers.add("Tc-Count", String.valueOf(countVoice));
            }
        }
        else {
            long countInternet = summaryQueryService.count(criteria
                    .setProduct(new ProductFilter().setEq(Product.INTERNET)));
            long countIptv = summaryQueryService.count(criteria
                    .setProduct(new ProductFilter().setEq(Product.IPTV)));
            long countVoice = summaryQueryService.count(criteria
                    .setProduct(new ProductFilter().setEq(Product.VOICE)));

            headers.add("Tc-Count", String.valueOf(countInternet));
            headers.add("Tc-Count", String.valueOf(countIptv));
            headers.add("Tc-Count", String.valueOf(countVoice));
        }
    }

}
