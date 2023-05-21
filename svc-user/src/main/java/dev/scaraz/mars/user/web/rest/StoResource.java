package dev.scaraz.mars.user.web.rest;

import dev.scaraz.mars.common.domain.general.StoDTO;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.user.mapper.StoMapper;
import dev.scaraz.mars.user.service.query.StoQueryService;
import dev.scaraz.mars.user.web.criteria.StoCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/sto")
public class StoResource {

    private final StoMapper mapper;
    private final StoQueryService queryService;

    @GetMapping
    public ResponseEntity<?> findAll(StoCriteria criteria, Pageable pageable) {
        Page<StoDTO> page = queryService.findAll(criteria, pageable)
                .map(mapper::toDTO);
        return ResourceUtil.pagination(page, "/sto");
    }

}
