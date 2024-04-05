package dev.scaraz.mars.core.web.rest.admins;

import dev.scaraz.mars.common.utils.AuthorityConstant;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.credential.AccountApproval;
import dev.scaraz.mars.core.query.AccountApprovalQueryService;
import dev.scaraz.mars.core.query.criteria.AccountApprovalCriteria;
import dev.scaraz.mars.core.service.credential.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/approvals")
public class UserApprovalResource {

    private final AccountService accountService;
    private final AccountApprovalQueryService accountApprovalQueryService;

    @GetMapping
    @PreAuthorize(AuthorityConstant.HAS_ROLE_ADMIN)
    public ResponseEntity<?> findAllApprovals(AccountApprovalCriteria criteria, Pageable pageable) {
        Page<AccountApproval> page = accountApprovalQueryService.findAll(criteria, pageable);
        return ResourceUtil.pagination(page, "/user/approvals");
    }

    @PostMapping
    @PreAuthorize(AuthorityConstant.HAS_ROLE_ADMIN)
    public ResponseEntity<?> approveUsersByApprovalIds(
            @RequestParam boolean approved,
            @RequestBody Collection<String> approvalIds
    ) {
        log.info("ACCEPTING APPROVALS -- APPROVED={} DATA={}", approved, approvalIds);
        approvalIds.forEach(id -> accountService.approval(id, approved));
        return ResponseEntity.ok().build();
    }

}
