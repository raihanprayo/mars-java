package dev.scaraz.mars.core.repository.cache;

import dev.scaraz.mars.core.domain.cache.PendingQueue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingQueueRepo extends CrudRepository<PendingQueue, String> {

}
