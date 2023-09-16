package dev.scaraz.mars.app.witel.repository;

import dev.scaraz.mars.app.witel.domain.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionRepo extends JpaRepository<Solution, String>, JpaSpecificationExecutor<Solution> {
}
