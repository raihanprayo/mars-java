package dev.scaraz.mars.app.administration.web.rest;

import dev.scaraz.mars.app.administration.config.security.SecurityUtil;
import dev.scaraz.mars.app.administration.domain.db.Config;
import dev.scaraz.mars.app.administration.repository.db.ConfigRepo;
import dev.scaraz.mars.app.administration.repository.db.ConfigTagRepo;
import dev.scaraz.mars.app.administration.service.app.ConfigService;
import dev.scaraz.mars.app.administration.web.dto.ConfigMapDTO;
import dev.scaraz.mars.common.domain.ConfigDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/config")
public class ConfigResource {

    private final ConfigRepo configRepo;
    private final ConfigTagRepo configTagRepo;
    private final ConfigService configService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<Config> configs = configRepo.findAllByWitelIsNullOrWitelEquals(SecurityUtil.getAccount().getWitel());
        ConfigMapDTO map = new ConfigMapDTO();
        for (Config config : configs) {
            if (config.getWitel() == null)
                map.getDefaults().put(config.getKey(), config);
            else
                map.getWitels().put(config.getKey(), config);
        }

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/tag")
    public ResponseEntity<?> findAllTags() {
        return new ResponseEntity<>(
                configTagRepo.findAll(),
                HttpStatus.OK
        );
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<?> findByTag(@PathVariable String tag) {
        List<Config> configs = configRepo.findAllByTagNameAndWitelIsNullOrWitelEquals(
                tag,
                SecurityUtil.getAccount().getWitel()
        );

        ConfigMapDTO map = new ConfigMapDTO();
        for (Config config : configs) {
            if (config.getWitel() == null)
                map.getDefaults().put(config.getKey(), config);
            else
                map.getWitels().put(config.getKey(), config);
        }

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody List<ConfigDTO> configs) {
        List<Config> list = configs.stream().map(configService::update)
                .collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

}
