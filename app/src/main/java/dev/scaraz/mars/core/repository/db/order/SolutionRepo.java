package dev.scaraz.mars.core.repository.db.order;

import dev.scaraz.mars.core.domain.order.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface SolutionRepo extends
        JpaRepository<Solution, Long>,
        JpaSpecificationExecutor<Solution> {

    Optional<Solution> findByName(String name);

    void deleteAllByIdInAndDeleteableIsTrue(Collection<Long> ids);
}
