package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/app")
public class AppResource {

    private final UserQueryService queryService;

    @GetMapping("/test")
    public ResponseEntity<?> testRoute(UserCriteria criteria) {
        log.info("CRITERIA {}", criteria);
        return new ResponseEntity<>(
                queryService.findAll(criteria),
                HttpStatus.OK
        );
    }

}
