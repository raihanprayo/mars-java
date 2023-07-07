package dev.scaraz.mars.v1.core.repository.order;

import dev.scaraz.mars.v1.core.domain.order.IssueParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueParamRepo extends JpaRepository<IssueParam, Long> {
}
