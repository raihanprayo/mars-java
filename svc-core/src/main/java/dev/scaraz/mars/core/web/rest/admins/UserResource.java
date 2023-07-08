package dev.scaraz.mars.core.web.rest.admins;

import dev.scaraz.mars.common.domain.request.CreateUserDTO;
import dev.scaraz.mars.common.domain.request.UserPasswordUpdateDTO;
import dev.scaraz.mars.common.domain.request.UpdateUserDashboardDTO;
import dev.scaraz.mars.common.domain.response.UserDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.credential.AccountApproval;
import dev.scaraz.mars.core.mapper.CredentialMapper;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.repository.db.credential.AccountApprovalRepo;
import dev.scaraz.mars.core.service.credential.UserService;
import dev.scaraz.mars.core.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/user")
public class UserResource {

    private final PasswordEncoder passwordEncoder;
    private final CredentialMapper credentialMapper;

    private final UserService userService;
    private final UserQueryService userQueryService;
    private final AccountApprovalRepo accountApprovalRepo;

    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "false") boolean plain,
            @RequestParam(defaultValue = "false") boolean mapped,
            UserCriteria criteria,
            Pageable pageable
    ) {
        return ResourceUtil.plainMappedResponse(plain, mapped,
                "/user",
                () -> userQueryService.findAll(criteria, pageable)
                        .map(credentialMapper::toDTO),
                () -> userQueryService.findAll(criteria).stream()
                        .map(credentialMapper::toDTO)
                        .collect(Collectors.toList()),
                UserDTO::getId);
    }

    @GetMapping("/detail/{nik}")
    public ResponseEntity<?> findByNik(@PathVariable String nik) {
        return ResponseEntity.ok(userQueryService.findByNik(nik));
    }

    @GetMapping("/approvals")
    public ResponseEntity<?> findAllApprovals(Pageable pageable) {
        Page<AccountApproval> page = accountApprovalRepo.findAll(pageable);
        return ResourceUtil.pagination(page, "/user/approvals");
    }

    @PostMapping("/approvals")
    public ResponseEntity<?> approveUsersByApprovalIds(
            @RequestParam boolean approved,
            @RequestBody Collection<String> approvalIds
    ) {
        log.info("ACCEPTING APPROVALS -- APPROVED={} DATA={}", approved, approvalIds);
        approvalIds.forEach(id -> userService.approval(id, approved));
        return ResponseEntity.ok().build();
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid CreateUserDTO req) {
        log.info("NEW USER DASHBOARD REGISTRATION -- {}", req);
        Account account = userService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(credentialMapper.toDTO(account));
    }

    @PutMapping("/partial/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable String userId,
            @RequestBody UpdateUserDashboardDTO req
    ) {
        Account account = userService.updatePartial(userId, req);
        return ResponseEntity.ok(credentialMapper.toDTO(account));
    }

    @PutMapping("/partial")
    public ResponseEntity<?> updateUser(
            @RequestBody UpdateUserDashboardDTO req
    ) {
        Account currentAccount = SecurityUtil.getCurrentUser();
        Account account = userService.updatePartial(currentAccount.getId(), req);
        return ResponseEntity.ok(credentialMapper.toDTO(account));
    }

    @PutMapping("/password/{userId}")
    public ResponseEntity<?> updateUserPassword(
            @PathVariable String userId,
            @RequestBody UserPasswordUpdateDTO updatePassDTO
    ) {
        Account account = userQueryService.findById(userId);
        boolean matches = passwordEncoder.matches(updatePassDTO.getOldPass(), account.getPassword());
        if (!matches)
            throw new BadRequestException("Password lama tidak sama!");

        boolean newPassEqOld = passwordEncoder.matches(updatePassDTO.getNewPass(), account.getPassword());
        if (newPassEqOld)
            throw new BadRequestException("Password baru tidak boleh sama dengan password lama");

        userService.updatePassword(account, updatePassDTO.getNewPass());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
