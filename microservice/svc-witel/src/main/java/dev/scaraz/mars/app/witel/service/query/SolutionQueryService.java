package dev.scaraz.mars.app.witel.service.query;

import dev.scaraz.mars.app.witel.domain.Solution;
import dev.scaraz.mars.app.witel.web.criteria.SolutionCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SolutionQueryService {
    List<Solution> findAll();

    Page<Solution> findAll(Pageable pageable);

    List<Solution> findAll(SolutionCriteria criteria);

    Page<Solution> findAll(SolutionCriteria criteria, Pageable pageable);

    long count();

    long count(SolutionCriteria criteria);

    Solution findById(String id);
}
