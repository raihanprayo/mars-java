package dev.scaraz.mars.core.service.app.impl;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.core.datasource.domain.Sto;
import dev.scaraz.mars.core.datasource.repo.StoRepo;
import dev.scaraz.mars.core.service.app.StoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor

@Service
public class StoServiceImpl implements StoService {

    private final StoRepo repo;

    @Override
    public Sto save(Sto sto) {
        return repo.save(sto);
    }

    @Override
    public Sto create(Sto sto) {
        log.info("CREATE NEW STO -- {}", sto);
        if (repo.existsByWitelAndAlias(sto.getWitel(), sto.getAlias()))
            throw BadRequestException.duplicateEntity(Sto.class, "witel/alias", String.join("/", sto.getWitel().name(), sto.getAlias()));
        return save(sto);
    }
}
