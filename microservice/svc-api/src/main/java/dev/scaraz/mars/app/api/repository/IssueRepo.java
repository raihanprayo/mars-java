package dev.scaraz.mars.app.api.repository;

import dev.scaraz.mars.app.api.domain.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepo extends JpaRepository<Issue, String> {
}
