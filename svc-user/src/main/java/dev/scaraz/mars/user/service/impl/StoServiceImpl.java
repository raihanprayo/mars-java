package dev.scaraz.mars.user.service.impl;

import dev.scaraz.mars.user.domain.Sto;
import dev.scaraz.mars.user.repository.db.StoRepo;
import dev.scaraz.mars.user.service.StoService;
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

}
