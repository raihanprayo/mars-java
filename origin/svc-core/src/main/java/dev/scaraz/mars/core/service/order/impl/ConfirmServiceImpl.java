package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.core.domain.order.TicketConfirm;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.repository.db.order.TicketConfirmRepo;
import dev.scaraz.mars.core.service.order.ConfirmService;
import dev.scaraz.mars.core.service.order.TicketFlowService;
import dev.scaraz.mars.core.service.order.flow.CloseFlowService;
import dev.scaraz.mars.core.service.order.flow.DispatchFlowService;
import dev.scaraz.mars.core.service.order.flow.PendingFlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static dev.scaraz.mars.common.utils.AppConstants.Cache;
import static dev.scaraz.mars.common.utils.AppConstants.Cache.TC_CONFIRM_NS;

@Slf4j
@RequiredArgsConstructor

@Service
public class ConfirmServiceImpl implements ConfirmService {

    private final TicketConfirmRepo repo;
    private final StringRedisTemplate stringRedisTemplate;


    @Lazy
    private final TicketQueryService queryService;
    @Lazy
    private final TicketFlowService flowService;

    @Lazy
    private final CloseFlowService closeFlowService;
    @Lazy
    private final PendingFlowService pendingFlowService;
    @Lazy
    private final DispatchFlowService dispatchFlowService;

    @Override
    public String getNamespace() {
        return TC_CONFIRM_NS;
    }

    @Override
    public void onExpired(String key) {
        long messageId = Long.parseLong(key);
        TicketConfirm confirm = repo.findById(messageId)
                .orElseThrow();
        try {
            switch (confirm.getStatus()) {
                case TicketConfirm.CLOSED:
                    closeFlowService.confirmClose(confirm.getValue(), false, new TicketStatusFormDTO());
                    break;
                case TicketConfirm.PENDING:
                    pendingFlowService.confirmPending(confirm.getValue(), false, new TicketStatusFormDTO());
                    break;
                case TicketConfirm.POST_PENDING:
                    pendingFlowService.askPostPending(confirm.getValue());
                    break;
                case TicketConfirm.POST_PENDING_CONFIRMATION:
                    pendingFlowService.confirmPostPending(confirm.getValue(), new TicketStatusFormDTO());
                    break;
            }
        }
        catch (Exception ex) {
        }

        deleteById(messageId);
    }

    @Async
    @PostConstruct
    public void onInit() throws InterruptedException {
        List<TicketConfirm> confirms = repo.findAll();

        if (confirms.isEmpty()) return;

        Thread.sleep(5000);

        log.info("Found {} expired confirmation(s)", confirms.size());
        for (TicketConfirm confirm : confirms) {
            BoundValueOperations<String, String> bounded = stringRedisTemplate.boundValueOps(confirm.getCacheKey());
            boolean hasKey = bounded.size() != null;

            if (hasKey) continue;
            log.info("REMOVING EXPIRED CONFIRMATION STATE -- ID {}", confirm.getId());

            switch (confirm.getStatus()) {
                case TicketConfirm.CLOSED:
                    closeFlowService.confirmClose(confirm.getValue(), false, new TicketStatusFormDTO());
                    deleteById(confirm.getId());
                    break;
                case TicketConfirm.PENDING:
                    pendingFlowService.confirmPending(confirm.getValue(), false, new TicketStatusFormDTO());
                    deleteById(confirm.getId());
                    break;
                case TicketConfirm.POST_PENDING:
                    pendingFlowService.askPostPending(confirm.getValue());
                    deleteById(confirm.getId());
                    break;
                case TicketConfirm.POST_PENDING_CONFIRMATION:
                    pendingFlowService.confirmPostPending(confirm.getValue(), new TicketStatusFormDTO());
                    deleteById(confirm.getId());
                    break;
            }
        }
    }


    @Override
    public TicketConfirm save(TicketConfirm o) {
        String messageId = String.valueOf(o.getId());
        stringRedisTemplate.boundSetOps(TC_CONFIRM_NS)
                .add(messageId);

        if (o.getTtl() > 0) {
            if (o.getValue() != null) {
                stringRedisTemplate.boundValueOps(Cache.j(TC_CONFIRM_NS, messageId))
                        .set(o.getValue(), o.getTtl(), TimeUnit.MINUTES);
            }
            else if (o.getIssueId() != null) {
                stringRedisTemplate.boundValueOps(Cache.j(TC_CONFIRM_NS, messageId))
                        .set(String.valueOf(o.getIssueId()), o.getTtl(), TimeUnit.MINUTES);
            }
        }
        else {
            if (o.getValue() != null) {
                stringRedisTemplate.boundValueOps(Cache.j(TC_CONFIRM_NS, messageId))
                        .set(o.getValue());
            }
            else if (o.getIssueId() != null) {
                stringRedisTemplate.boundValueOps(Cache.j(TC_CONFIRM_NS, messageId))
                        .set(o.getIssueId() + "");
            }
        }

        log.info("CREATE TICKET CONFIRMATION of status {} with message id {} -- {}", o.getStatus(), o.getId(), o.getValue());
        return repo.save(o);
    }

    @Override
    public void deleteById(long id) {
        log.debug("DELETE CONFIRM -- ID {}", id);
        repo.deleteById(id);
        deleteCache(id);
    }

    @Override
    public void deleteCache(long id) {
        log.debug("DELETE CONFIRM CACHE -- ID {}", id);
        String messageId = id + "";
        stringRedisTemplate.boundSetOps(TC_CONFIRM_NS)
                .remove(messageId);
        stringRedisTemplate.boundValueOps(Cache.j(TC_CONFIRM_NS, messageId))
                .getAndDelete();
    }

    @Override
    public TicketConfirm findById(long id) {
        return findByIdOpt(id).orElseThrow();
    }

    @Override
    public Optional<TicketConfirm> findByIdOpt(long id) {
        return repo.findById(id);
    }

    @Override
    public boolean existsById(long messageId) {
        return repo.existsById(messageId);
    }

    @Override
    public boolean existsByIdAndStatus(long messageId, String status) {
        return repo.existsByIdAndStatus(messageId, status);
    }

}
