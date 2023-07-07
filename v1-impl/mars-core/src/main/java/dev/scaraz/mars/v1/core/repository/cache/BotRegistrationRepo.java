package dev.scaraz.mars.v1.core.repository.cache;

import dev.scaraz.mars.v1.core.domain.cache.BotRegistration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotRegistrationRepo extends CrudRepository<BotRegistration, Long> {
}
