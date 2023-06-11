package dev.scaraz.mars.user.web.rest.audit;

import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.user.mapper.RoleMapper;
import dev.scaraz.mars.user.query.RoleQueryService;
import dev.scaraz.mars.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/role/audit")
@PreAuthorize("hasRole('" + AppConstants.Authority.ADMIN_ROLE + "')")
public class RoleAuditResource {

    private final RoleMapper mapper;
    private final RoleService service;
    private final RoleQueryService queryService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody List<String> roleNames) {
        return new ResponseEntity<>(
                service.create(roleNames).stream()
                        .map(mapper::toDTO)
                        .collect(Collectors.toList()),
                HttpStatus.CREATED
        );
    }

}
