package dev.scaraz.mars.core.repository.credential;

import dev.scaraz.mars.core.domain.credential.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCredentialRepo extends JpaRepository<UserCredential, String> {
}
