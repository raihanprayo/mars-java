package dev.scaraz.mars.core.repository.db;

import dev.scaraz.mars.core.domain.db.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionRepo extends
        JpaRepository<Solution, Long>,
        JpaSpecificationExecutor<Solution> {
}
