package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.utils.Util;
import dev.scaraz.mars.core.query.AccountQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/app")
public class AppResource {

    private final AccountQueryService queryService;

    @GetMapping("/test")
    public void testRoute() {
        Duration duration = Duration.ofHours(1);
        log.debug("{}", Util.durationDescribe(duration));
    }

}
