package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.*;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.criteria.AgentCriteria;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import dev.scaraz.mars.core.query.spec.TicketSpecBuilder;
import dev.scaraz.mars.core.repository.order.TicketRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class TicketQueryServiceImpl implements TicketQueryService {

    private final TicketRepo repo;
    private final TicketSpecBuilder specBuilder;

    @Override
    public Ticket findById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> NotFoundException.entity(Ticket.class, "id", id));
    }

    @Override
    public Ticket findByIdOrNo(String idOrNo) {
        return repo.findByIdOrNo(idOrNo, idOrNo)
                .orElseThrow(() -> NotFoundException.entity(Ticket.class, "id/no", idOrNo));
    }

    @Override
    public Ticket findByMessageId(Long messageId) {
        return repo.findOneByConfirmMessageId(messageId)
                .orElseThrow(() -> NotFoundException.entity(Ticket.class, "messageId", messageId));
    }

    @Override
    public List<Ticket> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<Ticket> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<Ticket> findAll(TicketCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    @Override
    public Page<Ticket> findAll(TicketCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public long count(TicketCriteria criteria) {
        return repo.count(specBuilder.createSpec(criteria));
    }

    @Override
    public Map<Product, Long> countProducts(@Nullable AgentCriteria agentCriteria) {
        EnumMap<Product, Long> map = new EnumMap<>(Product.class);
        TcStatusFilter statusFilter = new TcStatusFilter()
                .setEq(TcStatus.OPEN);

        map.put(Product.IPTV, count(TicketCriteria.builder()
                .status(statusFilter)
                .product(new ProductFilter().setEq(Product.IPTV))
                .agents(agentCriteria)
                .build()));

        map.put(Product.VOICE, count(TicketCriteria.builder()
                .status(statusFilter)
                .product(new ProductFilter().setEq(Product.VOICE))
                .agents(agentCriteria)
                .build()));

        map.put(Product.INTERNET, count(TicketCriteria.builder()
                .status(statusFilter)
                .product(new ProductFilter().setEq(Product.INTERNET))
                .agents(agentCriteria)
                .build())
        );
        return map;
    }

    @Override
    public int countGaul(long issueId, String serviceNo) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(7);
        return repo.countByServiceNoAndIssueIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
                serviceNo,
                issueId,
                weekAgo.toInstant(ZoneOffset.of("+07")),
                now.toInstant(ZoneOffset.of("+07"))
        );
    }

}
