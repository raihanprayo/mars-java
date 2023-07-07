package dev.scaraz.mars.v1.admin.repository.db.app;

import dev.scaraz.mars.v1.admin.domain.app.Sto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StoRepo extends JpaRepository<Sto, String>, JpaSpecificationExecutor<Sto> {
}
