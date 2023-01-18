package dev.scaraz.mars.core.web.rest.admins;

import dev.scaraz.mars.common.domain.request.CreateUserDTO;
import dev.scaraz.mars.common.domain.request.UserUpdateDashboardDTO;
import dev.scaraz.mars.common.domain.response.UserDTO;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.mapper.CredentialMapper;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.service.credential.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/user")
public class UserResource {

    private final CredentialMapper credentialMapper;

    private final UserService userService;
    private final UserQueryService userQueryService;

    @GetMapping
    public ResponseEntity<?> findAll(UserCriteria criteria, Pageable pageable) {
        Page<UserDTO> page = userQueryService.findAll(criteria, pageable)
                .map(credentialMapper::toDTO);
        return ResourceUtil.pagination(page, "/user");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid CreateUserDTO req) {
        User user = userService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(credentialMapper.toDTO(user));
    }

    @PutMapping("/partial/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable String userId,
            @RequestBody UserUpdateDashboardDTO req
    ) {
        User user = userService.updatePartial(userId, req);
        return ResponseEntity.ok(credentialMapper.toDTO(user));
    }

}
