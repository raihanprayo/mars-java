package dev.scaraz.mars.app.api.repository;

import dev.scaraz.mars.app.api.domain.Sto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoRepo extends JpaRepository<Sto, String> {
}
