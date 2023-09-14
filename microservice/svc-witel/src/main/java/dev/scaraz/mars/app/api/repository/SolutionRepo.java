package dev.scaraz.mars.app.api.repository;

import dev.scaraz.mars.app.api.domain.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionRepo extends JpaRepository<Solution, String> {
}
