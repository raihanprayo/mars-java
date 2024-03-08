package dev.scaraz.mars.core.web.rest.admins;

import dev.scaraz.mars.common.domain.request.CreateUserDTO;
import dev.scaraz.mars.common.domain.request.UpdateUserDashboardDTO;
import dev.scaraz.mars.common.domain.request.UserPasswordUpdateDTO;
import dev.scaraz.mars.common.domain.response.UserDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.utils.AuthorityConstant;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.credential.AccountCredential;
import dev.scaraz.mars.core.mapper.CredentialMapper;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.service.credential.AccountService;
import dev.scaraz.mars.security.MarsPasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/user")
public class UserResource {

    private final MarsPasswordEncoder passwordEncoder;
    private final CredentialMapper credentialMapper;

    private final AccountService accountService;
    private final AccountQueryService accountQueryService;

    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "false") boolean plain,
            @RequestParam(defaultValue = "false") boolean mapped,
            UserCriteria criteria,
            Pageable pageable
    ) {
        return ResourceUtil.plainMappedResponse(plain, mapped,
                "/user",
                () -> accountQueryService.findAll(criteria, pageable)
                        .map(credentialMapper::toDTO),
                () -> accountQueryService.findAll(criteria).stream()
                        .map(credentialMapper::toDTO)
                        .collect(Collectors.toList()),
                UserDTO::getId);
    }

    @GetMapping("/detail/{nik}")
    public ResponseEntity<?> findByNik(@PathVariable String nik) {
        UserDTO userDTO = credentialMapper.toDTO(accountQueryService.findByNik(nik));
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/register")
    @PreAuthorize(AuthorityConstant.HAS_ROLE_ADMIN)
    public ResponseEntity<?> register(@RequestBody @Valid CreateUserDTO req) {
        log.info("NEW USER DASHBOARD REGISTRATION -- {}", req);
        Account account = accountService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(credentialMapper.toDTO(account));
    }

    @PutMapping("/partial/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable String userId,
            @RequestBody UpdateUserDashboardDTO req
    ) {
        Account account = accountService.updatePartial(userId, req);
        return ResponseEntity.ok(credentialMapper.toDTO(account));
    }

    @PutMapping("/partial")
    public ResponseEntity<?> updateUser(
            @RequestBody UpdateUserDashboardDTO req
    ) {
        Account currentAccount = accountQueryService.findByCurrentAccess();
        Account account = accountService.updatePartial(currentAccount.getId(), req);
        return ResponseEntity.ok(credentialMapper.toDTO(account));
    }

    @PutMapping("/password/{userId}")
    public ResponseEntity<?> updateUserPassword(
            @PathVariable String userId,
            @RequestBody UserPasswordUpdateDTO updatePassDTO
    ) {
        Account account = accountQueryService.findById(userId);

        log.debug("Account Credentials: {}", account.getCredentials().stream()
                .map(AccountCredential::getPriority)
                .collect(Collectors.toList()));
        boolean matches = passwordEncoder.matches(updatePassDTO.getOldPass(), account.getCredential());
        if (!matches)
            throw new BadRequestException("Password lama tidak sesuai!");

//        boolean newPassEqOld = passwordEncoder.matches(updatePassDTO.getNewPass(), account.getPassword());
//        if (newPassEqOld)
//            throw new BadRequestException("Password baru tidak boleh sama dengan password lama");

        accountService.updatePassword(account, updatePassDTO.getNewPass());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
