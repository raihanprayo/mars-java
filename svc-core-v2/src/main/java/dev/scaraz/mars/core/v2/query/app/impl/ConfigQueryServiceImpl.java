package dev.scaraz.mars.core.v2.query.app.impl;

import dev.scaraz.mars.core.v2.domain.app.Config;
import dev.scaraz.mars.core.v2.query.app.ConfigQueryService;
import dev.scaraz.mars.core.v2.repository.db.app.ConfigRepo;
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

    @Override
    public List<Config> findAll() {
        return repo.findAll();
    }

    @Override
    public List<Config> findAllByTag(String tag) {
        return repo.findAllByTagName(tag);
    }

    @Override
    public Page<Config> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public Page<Config> findAllByTag(String tag, Pageable pageable) {
        return repo.findAllByTagName(tag, pageable);
    }

    @Override
    public long count() {
        return repo.count();
    }

}
