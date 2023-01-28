package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.core.domain.order.TicketConfirm;
import dev.scaraz.mars.core.tools.CacheExpireListener;

public interface TicketConfirmService extends CacheExpireListener {
    TicketConfirm save(TicketConfirm o);

    void deleteById(long id);

    void deleteCache(long id);

    TicketConfirm findById(long id);

    boolean existsById(long messageId);

    boolean existsByIdAndStatus(long messageId, String status);
}
