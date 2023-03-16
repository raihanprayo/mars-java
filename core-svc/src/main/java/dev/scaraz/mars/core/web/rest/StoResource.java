package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.order.Sto;
import dev.scaraz.mars.core.query.StoQueryService;
import dev.scaraz.mars.core.query.criteria.StoCriteria;
import dev.scaraz.mars.core.service.order.StoService;
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
        Sto result = service.create(sto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

}
