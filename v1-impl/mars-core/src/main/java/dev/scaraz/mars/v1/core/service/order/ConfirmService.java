package dev.scaraz.mars.v1.core.service.order;

import dev.scaraz.mars.v1.core.domain.order.TicketConfirm;
import dev.scaraz.mars.v1.core.tools.CacheExpireListener;

public interface ConfirmService extends CacheExpireListener {
    TicketConfirm save(TicketConfirm o);

    void deleteById(long id);

    void deleteCache(long id);

    TicketConfirm findById(long id);

    boolean existsById(long messageId);

    boolean existsByIdAndStatus(long messageId, String status);
}
