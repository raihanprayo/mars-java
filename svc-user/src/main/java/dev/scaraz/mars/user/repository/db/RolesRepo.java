package dev.scaraz.mars.user.repository.db;

import dev.scaraz.mars.user.domain.db.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolesRepo extends JpaRepository<Roles, Long> {

    void deleteAllByUserId(String userId);

    List<Roles> findAllByUserId(String userId);

}
