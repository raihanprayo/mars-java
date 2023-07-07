package dev.scaraz.mars.core.repository.order;

import dev.scaraz.mars.core.domain.order.TicketConfirm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketConfirmRepo extends JpaRepository<TicketConfirm, Long> {

    Optional<TicketConfirm> findByValueAndStatus(String value, String status);

    boolean existsByIdAndStatus(long messageId, String status);

}
