package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.domain.ConfigDTO;
import dev.scaraz.mars.core.mapper.ConfigMapper;
import dev.scaraz.mars.core.repository.db.ConfigRepo;
import dev.scaraz.mars.core.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/app/config")
public class ConfigResource {

//    private final AppConfigRepo configRepo;

    private final ConfigMapper mapper;
    private final ConfigRepo configRepo;
    private final ConfigService configService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(mapper.toDTO(configRepo.findAll()));
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody List<ConfigDTO> configs) {
        List<ConfigDTO> updated = new ArrayList<>();

        for (ConfigDTO config : configs)
            updated.add(mapper.toDTO(configService.update(config)));

        return ResponseEntity.ok(updated);
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<?> getByTag(@PathVariable String tag) {
        return ResponseEntity.ok(mapper.toDTO(configService.getByTag(tag)));
    }

    @GetMapping("/tags")
    public ResponseEntity<?> getTagLists() {
        return ResponseEntity.ok(configService.getTagList());
    }


}
