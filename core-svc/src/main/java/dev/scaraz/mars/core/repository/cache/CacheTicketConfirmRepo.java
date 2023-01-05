package dev.scaraz.mars.core.repository.cache;

import dev.scaraz.mars.core.domain.cache.CacheTicketConfirm;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CacheTicketConfirmRepo extends CrudRepository<CacheTicketConfirm, Long> {
}
