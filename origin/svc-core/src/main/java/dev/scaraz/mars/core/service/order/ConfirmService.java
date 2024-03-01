package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.core.domain.order.TicketConfirm;
import dev.scaraz.mars.core.tools.CacheExpireListener;

import java.util.Optional;

public interface ConfirmService extends CacheExpireListener {
    TicketConfirm save(TicketConfirm o);

    void deleteById(long id);

    void deleteCache(long id);

    TicketConfirm findById(long id);

    Optional<TicketConfirm> findByIdOpt(long id);

    boolean existsById(long messageId);

    boolean existsByIdAndStatus(long messageId, String statuse);
}
