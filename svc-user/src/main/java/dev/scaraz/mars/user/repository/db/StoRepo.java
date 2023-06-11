package dev.scaraz.mars.user.repository.db;

import dev.scaraz.mars.user.domain.db.Sto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoRepo extends JpaRepository<Sto, String>, JpaSpecificationExecutor<Sto> {

    Optional<Sto> findByIdOrName(String id, String name);

}
