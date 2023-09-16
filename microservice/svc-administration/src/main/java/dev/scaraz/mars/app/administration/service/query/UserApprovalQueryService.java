package dev.scaraz.mars.app.administration.service.query;

import dev.scaraz.mars.app.administration.domain.db.UserApproval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserApprovalQueryService {
    List<UserApproval> findAll();

    Page<UserApproval> findAll(Pageable pageable);

    UserApproval findByIdOrNo(String idOrNo);
}
