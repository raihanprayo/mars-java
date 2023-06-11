package dev.scaraz.mars.core.repository.db;

import dev.scaraz.mars.core.domain.db.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepo extends JpaRepository<Issue, Long> {
}
