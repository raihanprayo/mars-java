package dev.scaraz.mars.v1.core.web.rest;

import dev.scaraz.mars.v1.core.domain.AppConfig;
import dev.scaraz.mars.v1.core.repository.AppConfigRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/app/config")
public class AppConfigResource {

    private final AppConfigRepo configRepo;

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(
                configRepo.findAll(Sort.by(Sort.Order.asc("id")))
        );
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody List<AppConfig> configs) {
        return ResponseEntity.ok(
                configRepo.saveAll(configs)
        );
    }
}
