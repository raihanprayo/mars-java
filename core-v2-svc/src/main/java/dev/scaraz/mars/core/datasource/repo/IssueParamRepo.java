package dev.scaraz.mars.core.datasource.repo;

import dev.scaraz.mars.core.datasource.domain.IssueParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueParamRepo extends JpaRepository<IssueParam, Long> {
}
