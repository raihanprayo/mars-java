package dev.scaraz.mars.app.telegram.repository;

import dev.scaraz.mars.app.telegram.domain.UpdateCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpdateCacheRepo extends CrudRepository<UpdateCache, Integer> {
}
