package dev.scaraz.mars.core.v2.service.app.impl;

import dev.scaraz.mars.core.v2.domain.app.Sto;
import dev.scaraz.mars.core.v2.repository.db.app.StoRepo;
import dev.scaraz.mars.core.v2.service.app.StoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoServiceImpl implements StoService {

    private final StoRepo repo;

    @Override
    public Sto save(Sto o) {
        return repo.save(o);
    }

}
