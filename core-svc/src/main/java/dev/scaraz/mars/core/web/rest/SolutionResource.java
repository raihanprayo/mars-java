package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.domain.request.CreateSolutionDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.utils.ResourceUtil;
import dev.scaraz.mars.core.domain.order.Solution;
import dev.scaraz.mars.core.repository.order.SolutionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/solution")
public class SolutionResource {

    private final SolutionRepo repo;

    @GetMapping
    public ResponseEntity<?> findAll(Pageable pageable) {
        Page<Solution> page = repo.findAll(pageable);
        return ResourceUtil.pagination(page, "/solution");
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateSolutionDTO solution) {
        try {
            Solution ns = Solution.builder()
                    .name(solution.getName())
                    .description(solution.getDescription())
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(repo.save(ns));
        }
        catch (Exception ex) {
            if (ex.getMessage().contains("t_solution_name_key"))
                throw BadRequestException.duplicateEntity(Solution.class, "name", solution.getName());

            throw ex;
        }
    }

}
