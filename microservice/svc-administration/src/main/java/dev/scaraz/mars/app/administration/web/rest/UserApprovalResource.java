package dev.scaraz.mars.app.administration.web.rest;

import dev.scaraz.mars.app.administration.service.app.UserService;
import dev.scaraz.mars.app.administration.service.query.UserApprovalQueryService;
import dev.scaraz.mars.common.utils.ResourceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/user/approvals")
public class UserApprovalResource {

    private final UserService userService;

    private final UserApprovalQueryService userApprovalQueryService;

    @GetMapping
    public ResponseEntity<?> findAllApprovals(Pageable pageable) {
        return ResourceUtil.pagination(
                userApprovalQueryService.findAll(pageable),
                "/user/approvals"
        );
    }

    @PostMapping
    public ResponseEntity<?> updateApprovals(
            @RequestParam boolean approved,
            @RequestBody Collection<String> ids
    ) {
        ids.forEach(id -> userService.createUserFromApproval(id, approved));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{idOrNo}")
    public ResponseEntity<?> updateApprovals(
            @PathVariable String idOrNo,
            @RequestParam boolean approved
    ) {
        UserService.RegistrationResult result = userService.createUserFromApproval(idOrNo, approved);
        return new ResponseEntity<>(result.isOnHold() ? HttpStatus.OK : HttpStatus.CREATED);
    }

}
