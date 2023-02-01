package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.domain.request.CreateIssueDTO;
import dev.scaraz.mars.common.domain.request.UpdateIssueDTO;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.event.RefreshIssueInlineButtons;
import dev.scaraz.mars.core.domain.order.Issue;
import dev.scaraz.mars.core.query.IssueQueryService;
import dev.scaraz.mars.core.query.criteria.IssueCriteria;
import dev.scaraz.mars.core.service.order.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static dev.scaraz.mars.common.utils.AppConstants.RESET_ISSUE_INLINE_BTN_EVENT;

@RequiredArgsConstructor

@RestController
@RequestMapping("/issue")
public class IssueResource {

    private final IssueService service;
    private final IssueQueryService queryService;

    private final ApplicationEventPublisher eventPublisher;

    @GetMapping
    public ResponseEntity<?> findAll(IssueCriteria criteria, Pageable pageable) {
        return ResourceUtil.pagination(
                queryService.findAll(criteria, pageable),
                "/api/issue"
        );
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateIssueDTO newIssue) {
        Issue issue = service.create(newIssue);
        eventPublisher.publishEvent(new RefreshIssueInlineButtons());
        return new ResponseEntity<>(
                issue,
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable long id,
            @RequestBody UpdateIssueDTO issue
    ) {
        Issue update = service.update(id, issue);
        eventPublisher.publishEvent(new RefreshIssueInlineButtons());
        return ResponseEntity.ok(update);
    }

}
