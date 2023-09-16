package dev.scaraz.mars.app.administration.repository.cache;

import dev.scaraz.mars.app.administration.domain.cache.FormRegistrationCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormRegistrationCacheRepo extends JpaRepository<FormRegistrationCache, Long> {
}
