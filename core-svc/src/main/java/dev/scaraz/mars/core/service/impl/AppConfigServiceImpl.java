package dev.scaraz.mars.core.service.impl;

import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.domain.AppConfig;
import dev.scaraz.mars.core.repository.AppConfigRepo;
import dev.scaraz.mars.core.service.AppConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor

@Service
public class AppConfigServiceImpl implements AppConfigService {

    private final AppConfigRepo repo;

    @Override
    public AppConfig save(AppConfig config) {
        return repo.save(config);
    }

    @Override
    public AppConfig getById(long id) {
        return repo.findById(id)
                .orElseThrow();
    }

    @Override
    public AppConfig getCloseConfirm() {
        return getById(AppConstants.Config.CLOSE_CONFIRM_ID_INT);
    }

    @Override
    public AppConfig getAllowLogin() {
        return getById(AppConstants.Config.ALLOW_OTHER_WITEL_ID_BOOL);
    }

}
