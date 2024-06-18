package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.domain.request.CreateSolutionDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.symptom.Solution;
import dev.scaraz.mars.core.query.SolutionQueryService;
import dev.scaraz.mars.core.query.criteria.SolutionCriteria;
import dev.scaraz.mars.core.repository.db.order.SolutionRepo;
import dev.scaraz.mars.core.service.order.SolutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/solution")
public class SolutionResource {

    private final SolutionRepo repo;
    private final SolutionService service;
    private final SolutionQueryService queryService;

    @GetMapping
    public ResponseEntity<?> findAll(
            SolutionCriteria criteria,
            Pageable pageable
    ) {
        Page<Solution> page = queryService.findAll(criteria, pageable);
        return ResourceUtil.pagination(page, "/solution");
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateSolutionDTO solution) {
        try {
            Solution ns = Solution.builder()
                    .name(solution.getName())
                    .description(solution.getDescription())
                    .product(solution.getProduct())
                    .showable(true)
                    .deleteable(true)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(service.save(ns));
        }
        catch (Exception ex) {
            if (ex.getMessage().contains("t_solution_name_key"))
                throw BadRequestException.duplicateEntity(Solution.class, "name", solution.getName());

            throw ex;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable long id,
            @RequestBody Solution update
    ) {
        return ResponseEntity.ok(service.update(id, update));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteByIds(
            @RequestBody List<Long> ids
    ) {
        service.deleteByIds(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
