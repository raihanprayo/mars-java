package dev.scaraz.mars.core.repository.cache;

import dev.scaraz.mars.core.domain.cache.StatusConfirm;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusConfirmRepo extends CrudRepository<StatusConfirm, Long> {
}
