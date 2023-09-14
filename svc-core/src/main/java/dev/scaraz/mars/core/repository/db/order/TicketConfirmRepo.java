package dev.scaraz.mars.core.repository.db.order;

import dev.scaraz.mars.core.domain.order.TicketConfirm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface TicketConfirmRepo extends JpaRepository<TicketConfirm, Long> {

    Optional<TicketConfirm> findByValueAndStatus(String value, String status);

    boolean existsByIdAndStatusIn(long messageId, Collection<String> statuses);
    boolean existsByIdAndStatus(long messageId, String status);

}
