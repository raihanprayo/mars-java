package dev.scaraz.mars.v1.admin.web.rest;

import dev.scaraz.mars.v1.admin.domain.app.Config;
import dev.scaraz.mars.v1.admin.mapper.ConfigMapper;
import dev.scaraz.mars.v1.admin.services.app.ConfigService;
import dev.scaraz.mars.v1.admin.services.query.ConfigQueryService;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.common.domain.ConfigDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/config")
public class ConfigResource {

    private final ConfigMapper mapper;
    private final ConfigService service;
    private final ConfigQueryService queryService;

    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(required = false) String tag,
            Pageable pageable
    ) {
        Page<Config> page;
        if (StringUtils.isNoneBlank(tag))
            page = queryService.findAllByTag(tag, pageable);
        else
            page = queryService.findAll(pageable);

        return ResourceUtil.pagination(page.map(mapper::toDTO), "/config");
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody List<ConfigDTO> configs) {
        List<ConfigDTO> updates = configs.stream()
                .map(service::update)
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(updates);
    }

}
