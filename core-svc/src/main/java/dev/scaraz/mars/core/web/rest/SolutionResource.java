package dev.scaraz.mars.core.web.rest;

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
    public ResponseEntity<?> create(@RequestBody Solution solution) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(repo.save(solution));
    }

}
