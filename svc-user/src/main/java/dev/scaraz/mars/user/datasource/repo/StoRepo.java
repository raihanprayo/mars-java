package dev.scaraz.mars.user.datasource.repo;

import dev.scaraz.mars.user.datasource.domain.Sto;
import org.springframework.stereotype.Repository;

@Repository
public interface StoRepo extends WithSpecRepository<Sto, Integer> {

    boolean existsByAliasIgnoreCase(String alias);

}
