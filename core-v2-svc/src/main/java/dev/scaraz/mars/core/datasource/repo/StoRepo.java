package dev.scaraz.mars.core.datasource.repo;

import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.core.datasource.domain.Sto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoRepo extends JpaRepository<Sto, Long> {
    boolean existsByWitelAndAlias(Witel witel, String alias);
}
