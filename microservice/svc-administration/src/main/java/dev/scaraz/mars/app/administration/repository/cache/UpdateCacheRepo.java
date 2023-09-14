package dev.scaraz.mars.app.administration.repository.cache;

import dev.scaraz.mars.app.administration.domain.cache.UpdateCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpdateCacheRepo extends CrudRepository<UpdateCache, Integer> {
}
