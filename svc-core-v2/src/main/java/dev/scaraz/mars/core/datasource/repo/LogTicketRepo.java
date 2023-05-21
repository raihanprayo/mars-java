package dev.scaraz.mars.core.datasource.repo;

import dev.scaraz.mars.core.datasource.domain.LogTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogTicketRepo extends JpaRepository<LogTicket, String> {
}
