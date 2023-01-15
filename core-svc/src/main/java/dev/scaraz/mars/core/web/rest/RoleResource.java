package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.mapper.RoleMapper;
import dev.scaraz.mars.core.query.RoleQueryService;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/internal/role")
@Transactional(readOnly = true)
public class RoleResource {

    private final RoleMapper roleMapper;
    private final RoleQueryService roleQueryService;

    @GetMapping
    public ResponseEntity<?> findAll(RoleCriteria criteria, Pageable pageable) {
        Page<Role> page = roleQueryService.findAll(pageable);
        HttpHeaders headers = ResourceUtil.generatePaginationHeader(page, "/role");

        return new ResponseEntity<>(
                page.map(roleMapper::toDTO),
                headers,
                HttpStatus.OK
        );
    }

}
