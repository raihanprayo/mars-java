package dev.scaraz.mars.app.witel.repository;

import dev.scaraz.mars.app.witel.domain.IssueParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueParamRepo extends JpaRepository<IssueParam, Long> {
}
