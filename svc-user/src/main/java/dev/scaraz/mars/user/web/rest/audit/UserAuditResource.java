package dev.scaraz.mars.user.web.rest.audit;

import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.user.domain.db.MarsUser;
import dev.scaraz.mars.user.mapper.UserMapper;
import dev.scaraz.mars.user.query.UserQueryService;
import dev.scaraz.mars.user.service.UserService;
import dev.scaraz.mars.user.web.criteria.UserCriteria;
import dev.scaraz.mars.user.web.dto.CreateUserDTO;
import dev.scaraz.mars.user.web.dto.UpdateRoleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@PreAuthorize("hasRole('" + AppConstants.Authority.ADMIN_ROLE + "')")
public class UserAuditResource {

    private final UserMapper mapper;
    private final UserService service;
    private final UserQueryService queryService;

    @GetMapping
    public ResponseEntity<?> findAll(UserCriteria criteria, Pageable pageable) {
        return ResourceUtil.pagination(
                queryService.findAll(criteria, pageable).map(mapper::toDTO),
                "/user"
        );
    }

    @PostMapping(path = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody @Valid CreateUserDTO req) {
        MarsUser user = service.create(req);
        return new ResponseEntity<>(mapper.toDTO(user), HttpStatus.CREATED);
    }

    @PutMapping(path = "/update/role/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateRoles(
            @PathVariable String userId,
            @RequestBody @Valid UpdateRoleDTO req
    ) {
        MarsUser user = service.updateRole(userId, req);
        return new ResponseEntity<>(mapper.toDTO(user), HttpStatus.OK);
    }

}
