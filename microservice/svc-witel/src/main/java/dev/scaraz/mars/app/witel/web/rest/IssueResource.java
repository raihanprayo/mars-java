package dev.scaraz.mars.app.witel.web.rest;

import dev.scaraz.mars.app.witel.domain.Issue;
import dev.scaraz.mars.app.witel.service.app.IssueService;
import dev.scaraz.mars.app.witel.service.query.IssueQueryService;
import dev.scaraz.mars.app.witel.web.criteria.IssueCriteria;
import dev.scaraz.mars.common.domain.request.CreateIssueDTO;
import dev.scaraz.mars.common.domain.request.UpdateIssueDTO;
import dev.scaraz.mars.common.utils.ResourceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                "/issue"
        );
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateIssueDTO newIssue) {
        Issue issue = service.create(newIssue);
//        eventPublisher.publishEvent(new RefreshIssueInlineButtons());
        return new ResponseEntity<>(
                issue,
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable String id,
            @RequestBody UpdateIssueDTO issue
    ) {
        Issue update = service.update(id, issue);
//        eventPublisher.publishEvent(new RefreshIssueInlineButtons());
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        service.deleteByIds(List.of(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(
            path = "/bulk",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> restoreBulk(@RequestBody List<String> ids) {
        service.restoreByIds(ids);
//        eventPublisher.publishEvent(new RefreshIssueInlineButtons());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(
            path = "/bulk",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteBulk(@RequestBody List<String> ids) {
        service.deleteByIds(ids);
//        eventPublisher.publishEvent(new RefreshIssueInlineButtons());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
