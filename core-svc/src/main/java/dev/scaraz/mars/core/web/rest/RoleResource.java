package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.mapper.RoleMapper;
import dev.scaraz.mars.core.query.RoleQueryService;
import dev.scaraz.mars.core.query.criteria.GroupCriteria;
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
@RequestMapping("/role")
@Transactional(readOnly = true)
public class RoleResource {

    private final RoleMapper roleMapper;
    private final RoleQueryService roleQueryService;

    @GetMapping
    public ResponseEntity<?> applicationRoles(
            @RequestParam(defaultValue = "false") boolean all,
            Pageable pageable
    ) {
        Page<Role> page = all ?
                roleQueryService.findAll(pageable) :
                roleQueryService.findAllGroupIsNull(pageable);

        HttpHeaders headers = ResourceUtil.generatePaginationHeader(page, "/role");

        return new ResponseEntity<>(
                page.map(roleMapper::toDTO),
                headers,
                HttpStatus.OK
        );
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> applicationRoles(
            @PathVariable String groupId,
            Pageable pageable
    ) {
        Page<Role> page = roleQueryService.findAll(RoleCriteria.builder()
                        .group(GroupCriteria.builder()
                                .id(new StringFilter().setEq(groupId))
                                .build())
                        .build(),
                pageable);

        HttpHeaders headers = ResourceUtil.generatePaginationHeader(page, "/role/group/" + groupId);
        return new ResponseEntity<>(
                page.map(roleMapper::toDTO),
                headers,
                HttpStatus.OK
        );
    }

}
