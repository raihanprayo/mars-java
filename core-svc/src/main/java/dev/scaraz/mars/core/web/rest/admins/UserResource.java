package dev.scaraz.mars.core.web.rest.admins;

import dev.scaraz.mars.common.domain.request.CreateUserDTO;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.service.credential.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor

@PreAuthorize("hasRole('admin')")
@RestController
@RequestMapping("/internal/user")
public class UserResource {

    private final UserService userService;
    private final UserQueryService userQueryService;

    @GetMapping
    public ResponseEntity<?> findAll(UserCriteria criteria, Pageable pageable) {
        Page<User> page = userQueryService.findAll(criteria, pageable);
        return ResourceUtil.pagination(page, "/internal/admin");
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@ModelAttribute @Valid CreateUserDTO req) {
        User user = userService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(user);
    }

}
