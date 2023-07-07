package dev.scaraz.mars.core.repository.order;

import dev.scaraz.mars.core.domain.order.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionRepo extends
        JpaRepository<Solution, Long>,
        JpaSpecificationExecutor<Solution> {
}
