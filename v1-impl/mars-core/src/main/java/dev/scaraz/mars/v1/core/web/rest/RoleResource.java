package dev.scaraz.mars.v1.core.web.rest;

import dev.scaraz.mars.common.domain.response.RoleDTO;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.v1.core.domain.credential.Roles;
import dev.scaraz.mars.v1.core.mapper.RoleMapper;
import dev.scaraz.mars.v1.core.query.RoleQueryService;
import dev.scaraz.mars.v1.core.query.criteria.RoleCriteria;
import dev.scaraz.mars.v1.core.repository.credential.RolesRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/role")
@PreAuthorize("hasRole('admin')")
public class RoleResource {

    private final RoleMapper roleMapper;
    private final RoleQueryService roleQueryService;
    private final RolesRepo rolesRepo;

    @GetMapping
    public ResponseEntity<?> findAll(RoleCriteria criteria, Pageable pageable) {
        Page<RoleDTO> page = roleQueryService.findAll(criteria, pageable)
                .map(roleMapper::toDTO);

        return ResourceUtil.pagination(page, "/role");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getRoleByUser(@PathVariable String userId) {
        List<RoleDTO> roles = rolesRepo.findAllByUserId(userId).stream()
                .map(Roles::getRole)
                .map(roleMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

}
