package dev.scaraz.mars.app.administration.repository.cache;

import dev.scaraz.mars.app.administration.domain.cache.ImpersonateTokenCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImpersonateTokenCacheRepo extends JpaRepository<ImpersonateTokenCache, String> {
}
