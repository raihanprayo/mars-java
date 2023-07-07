package dev.scaraz.mars.v1.admin.web.rest;

import dev.scaraz.mars.v1.admin.domain.app.Sto;
import dev.scaraz.mars.v1.admin.services.app.StoService;
import dev.scaraz.mars.v1.admin.services.query.StoQueryService;
import dev.scaraz.mars.v1.admin.web.criteria.StoCriteria;
import dev.scaraz.mars.common.utils.ResourceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/sto")
public class StoResource {

    private final StoService service;
    private final StoQueryService queryService;

    @GetMapping
    public ResponseEntity<?> findAll(StoCriteria criteria, Pageable pageable) {
        Page<Sto> page = queryService.findAll(criteria, pageable);
        return ResourceUtil.pagination(page, "/sto");
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Sto sto) {
        return new ResponseEntity<>(service.create(sto), HttpStatus.CREATED);
    }

}
