package dev.scaraz.mars.core.repository.credential;

import dev.scaraz.mars.core.domain.credential.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepo extends JpaRepository<Roles, String> {
}
