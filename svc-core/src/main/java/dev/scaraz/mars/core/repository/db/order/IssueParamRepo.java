package dev.scaraz.mars.core.repository.db.order;

import dev.scaraz.mars.core.domain.order.IssueParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueParamRepo extends JpaRepository<IssueParam, Long> {
}
