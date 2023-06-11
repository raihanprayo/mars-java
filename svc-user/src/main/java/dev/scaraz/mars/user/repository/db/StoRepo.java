package dev.scaraz.mars.user.repository.db;

import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.user.domain.Sto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StoRepo extends JpaRepository<Sto, Integer>, JpaSpecificationExecutor<Sto> {

    boolean existsByWitelAndAlias(Witel witel, String alias);

}
