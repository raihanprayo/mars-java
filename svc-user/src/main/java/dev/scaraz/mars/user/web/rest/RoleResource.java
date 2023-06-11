package dev.scaraz.mars.user.web.rest;

import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.user.mapper.RoleMapper;
import dev.scaraz.mars.user.query.RoleQueryService;
import dev.scaraz.mars.user.service.RoleService;
import dev.scaraz.mars.user.web.criteria.RoleCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor

@RestController
@RequestMapping("/role")
public class RoleResource {

    private final RoleMapper mapper;
    private final RoleService service;
    private final RoleQueryService queryService;

    @GetMapping
    public ResponseEntity<?> findAll(RoleCriteria criteria, Pageable pageable) {
        return ResourceUtil.pagination(
                queryService.findAll(criteria, pageable).map(mapper::toDTO),
                "/role"
        );
    }

}
