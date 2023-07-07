package dev.scaraz.mars.core.repository.cache;

import dev.scaraz.mars.core.domain.cache.BotRegistration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotRegistrationRepo extends CrudRepository<BotRegistration, Long> {
}
