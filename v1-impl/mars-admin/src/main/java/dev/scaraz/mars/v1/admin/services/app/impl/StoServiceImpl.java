package dev.scaraz.mars.v1.admin.services.app.impl;

import dev.scaraz.mars.v1.admin.domain.app.Sto;
import dev.scaraz.mars.v1.admin.repository.db.app.StoRepo;
import dev.scaraz.mars.v1.admin.services.app.StoService;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        if (repo.existsById(sto.getCode()))
            throw BadRequestException.duplicateEntity(Sto.class, "code", sto.getCode());
        return save(sto);
    }

}
