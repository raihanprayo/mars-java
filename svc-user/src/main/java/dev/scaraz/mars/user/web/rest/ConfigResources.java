package dev.scaraz.mars.user.web.rest;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.user.domain.db.AppConfig;
import dev.scaraz.mars.user.domain.db.AppConfigCategory;
import dev.scaraz.mars.user.mapper.AppConfigMapper;
import dev.scaraz.mars.user.repository.db.AppConfigCategoryRepo;
import dev.scaraz.mars.user.repository.db.AppConfigRepo;
import dev.scaraz.mars.user.service.AppConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/config")
public class ConfigResources {

    private final AppConfigService appConfigService;
    private final AppConfigMapper appConfigMapper;

    private final AppConfigRepo repo;
    private final AppConfigCategoryRepo categoryRepo;

    @GetMapping
    @PreAuthorize("hasRole('" + AppConstants.Authority.ADMIN_ROLE + "')")
    public ResponseEntity<?> findAll() {
        return new ResponseEntity<>(
                repo.findAll().stream()
                        .map(appConfigMapper::toDTO)
                        .collect(Collectors.toSet()),
                HttpStatus.OK);
    }

    @GetMapping("/{categoryName}")
    public ResponseEntity<?> findAllByCategory(@PathVariable String categoryName) {
        AppConfigCategory category = categoryRepo.findById(categoryName)
                .orElseThrow(() -> NotFoundException.entity(AppConfigCategory.class, "id", categoryName));

        return new ResponseEntity<>(
                category.getConfigs().stream()
                        .map(appConfigMapper::toDTO)
                        .collect(Collectors.toSet()),
                HttpStatus.OK);
    }

    @GetMapping("/specified/{key}")
    public ResponseEntity<?> findByKey(@PathVariable String key) {
        AppConfig config = repo.findByName(key)
                .orElseThrow(() -> NotFoundException.entity(AppConfig.class, "name", key));

        return new ResponseEntity<>(appConfigMapper.toDTO(config), HttpStatus.OK);
    }

}
