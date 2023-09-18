package dev.scaraz.mars.app.administration.repository.cache;

import dev.scaraz.mars.app.administration.domain.cache.FormTicketRegistrationCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.function.Consumer;

@Repository
public interface FormTicketRegistrationCacheRepo extends CrudRepository<FormTicketRegistrationCache, Long> {

    default void update(long messageId,
                        Consumer<FormTicketRegistrationCache.FormTicketRegistrationCacheBuilder> callback) {
        findById(messageId)
                .ifPresent(c -> {
                    FormTicketRegistrationCache.FormTicketRegistrationCacheBuilder b = c.toBuilder();
                    callback.accept(b);
                    save(b.build());
                    deleteById(messageId);
                });
    }

}
