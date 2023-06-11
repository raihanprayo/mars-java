package dev.scaraz.mars.user.repository.db;

import dev.scaraz.mars.user.domain.db.MarsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarsUserRepo extends
        JpaRepository<MarsUser, String>,
        JpaSpecificationExecutor<MarsUser> {

    Optional<MarsUser> findByNik(String nik);
}
