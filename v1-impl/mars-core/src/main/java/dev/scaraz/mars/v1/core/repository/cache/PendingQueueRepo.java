package dev.scaraz.mars.v1.core.repository.cache;

import dev.scaraz.mars.v1.core.domain.cache.PendingQueue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingQueueRepo extends CrudRepository<PendingQueue, String> {

}
