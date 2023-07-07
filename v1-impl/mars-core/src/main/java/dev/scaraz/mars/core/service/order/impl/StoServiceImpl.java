package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.core.domain.order.Sto;
import dev.scaraz.mars.core.repository.order.StoRepo;
import dev.scaraz.mars.core.service.order.StoService;
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
    @Transactional
    public Sto create(Sto sto) {
        log.info("CREATE NEW STO -- {}", sto);
        if (repo.existsByWitelAndAlias(sto.witel, sto.getAlias()))
            throw BadRequestException.duplicateEntity(Sto.class, "witel/alias", String.join("/", sto.getWitel().name(), sto.getAlias()));
        return save(sto);
    }

}
