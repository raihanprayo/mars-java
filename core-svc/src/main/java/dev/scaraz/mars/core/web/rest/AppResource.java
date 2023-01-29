package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.service.PubSubService;
import dev.scaraz.mars.core.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/app")
public class AppResource {

    private final UserQueryService queryService;
    private final PubSubService pubSubService;

    @GetMapping("/test")
    public ResponseEntity<?> testRoute(UserCriteria criteria) {
        log.info("CRITERIA {}", criteria);
        return new ResponseEntity<>(
                queryService.findAll(criteria),
                HttpStatus.OK
        );
    }

    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        User user = SecurityUtil.getCurrentUser();
        return pubSubService.subscribe(user.getId());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void pushToAll(
            @RequestParam("event") String eventName,
            @RequestBody Object data
    ) {
        pubSubService.sendToAll(SseEmitter.event()
                .name(eventName)
                .data(data, MediaType.APPLICATION_JSON));
    }

}
