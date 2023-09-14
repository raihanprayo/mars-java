package dev.scaraz.mars.app.administration.repository.db;

import dev.scaraz.mars.app.administration.domain.db.UserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRegistrationRepo extends JpaRepository<UserRegistration, String> {

    Optional<UserRegistration> findByIdOrNo(String id, String no);

}
