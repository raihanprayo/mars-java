package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.core.domain.order.TicketConfirm;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.repository.order.TicketConfirmRepo;
import dev.scaraz.mars.core.service.order.TicketConfirmService;
import dev.scaraz.mars.core.service.order.TicketFlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static dev.scaraz.mars.common.utils.AppConstants.Cache;
import static dev.scaraz.mars.common.utils.AppConstants.Cache.TC_CONFIRM_NS;

@Slf4j
@RequiredArgsConstructor

@Service
public class TicketConfirmServiceImpl implements TicketConfirmService {

    private final TicketConfirmRepo repo;
    private final StringRedisTemplate stringRedisTemplate;


    @Lazy
    private final TicketQueryService queryService;
    @Lazy
    private final TicketFlowService flowService;

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
                    flowService.confirmClose(confirm.getNo(), false, new TicketStatusFormDTO());
                    break;
                case TicketConfirm.PENDING:
                    flowService.confirmPending(confirm.getNo(), false, new TicketStatusFormDTO());
                    break;
                case TicketConfirm.POST_PENDING:
                    flowService.askPostPending(confirm.getNo());
                    break;
                case TicketConfirm.POST_PENDING_CONFIRMATION:
                    flowService.confirmPostPending(confirm.getNo(), new TicketStatusFormDTO());
                    break;
            }
        }
        catch (Exception ex) {
        }

        deleteById(messageId);
    }


    @Override
    public TicketConfirm save(TicketConfirm o) {
        String messageId = o.getId() + "";
        stringRedisTemplate.boundSetOps(TC_CONFIRM_NS)
                .add(messageId);
        stringRedisTemplate.boundValueOps(Cache.j(TC_CONFIRM_NS, messageId))
                .set(o.getNo(), o.getTtl(), TimeUnit.MINUTES);
        return repo.save(o);
    }

    @Override
    public void deleteById(long id) {
        repo.deleteById(id);
        deleteCache(id);
    }

    @Override
    public void deleteCache(long id) {
        String messageId = id + "";
        stringRedisTemplate.boundSetOps(TC_CONFIRM_NS)
                .remove(messageId);
        stringRedisTemplate.boundValueOps(Cache.j(TC_CONFIRM_NS, messageId))
                .getAndDelete();
    }

    @Override
    public TicketConfirm findById(long id) {
        return repo.findById(id)
                .orElseThrow();
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
