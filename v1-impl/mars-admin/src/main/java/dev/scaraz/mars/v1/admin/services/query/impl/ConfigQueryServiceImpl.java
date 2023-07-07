package dev.scaraz.mars.v1.admin.services.query.impl;

import dev.scaraz.mars.v1.admin.domain.app.Config;
import dev.scaraz.mars.v1.admin.repository.db.app.ConfigRepo;
import dev.scaraz.mars.v1.admin.repository.db.app.ConfigTagRepo;
import dev.scaraz.mars.v1.admin.services.query.ConfigQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class ConfigQueryServiceImpl implements ConfigQueryService {

    private final ConfigRepo repo;
    private final ConfigTagRepo tagRepo;

    @Override
    public List<Config> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<Config> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<Config> findAllByTag(String tag) {
        return repo.findAllByTagName(tag);
    }

    @Override
    public Page<Config> findAllByTag(String tag, Pageable pageable) {
        return repo.findAllByTagName(tag, pageable);
    }

}
