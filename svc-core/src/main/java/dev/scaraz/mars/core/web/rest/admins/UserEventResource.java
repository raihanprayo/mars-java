package dev.scaraz.mars.core.web.rest.admins;

import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.query.AccountEventQueryService;
import dev.scaraz.mars.core.query.criteria.AccountEventCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/event")
@RequiredArgsConstructor
public class UserEventResource {

    private final AccountEventQueryService queryService;

    @GetMapping
    public ResponseEntity<?> findAll(AccountEventCriteria criteria, Pageable pageable) {
        return ResourceUtil.pagination(queryService.findAll(criteria, pageable), "/user/event");
    }

}
