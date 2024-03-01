package dev.scaraz.mars.core.repository.cache;

import dev.scaraz.mars.core.domain.cache.ForgotPassword;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgotPasswordRepo extends CrudRepository<ForgotPassword, String> {
}
