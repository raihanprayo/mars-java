package dev.scaraz.mars.core.repository.cache;


import dev.scaraz.mars.core.domain.cache.RegistrationApproval;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationApprovalRepo extends CrudRepository<RegistrationApproval, String> {
}
