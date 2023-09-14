package dev.scaraz.mars.app.administration.repository.db;

import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.app.administration.domain.db.Sto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoRepo extends JpaRepository<Sto, Integer>, JpaSpecificationExecutor<Sto> {

    boolean existsByWitelAndAlias(Witel witel, String alias);

    int countByWitel(Witel witel);
    List<Sto> findAllByWitel(Witel witel);

    Optional<Sto> findByWitelAndAliasIgnoreCase(Witel witel, String alias);

}
