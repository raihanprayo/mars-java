package dev.scaraz.mars.core.v2.repository.db.app;

import dev.scaraz.mars.core.v2.domain.app.Sto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StoRepo extends JpaRepository<Sto, String>, JpaSpecificationExecutor<Sto> {
}
