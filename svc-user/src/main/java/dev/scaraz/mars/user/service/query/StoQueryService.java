package dev.scaraz.mars.user.service.query;

import dev.scaraz.mars.user.datasource.domain.Sto;
import dev.scaraz.mars.user.web.criteria.StoCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StoQueryService {
    List<Sto> findAll(StoCriteria criteria);

    Page<Sto> findAll(StoCriteria criteria, Pageable pageable);
}
