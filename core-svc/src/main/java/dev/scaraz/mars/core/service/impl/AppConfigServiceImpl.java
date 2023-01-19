package dev.scaraz.mars.core.service.impl;

import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.domain.AppConfig;
import dev.scaraz.mars.core.repository.AppConfigRepo;
import dev.scaraz.mars.core.service.AppConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor

@Service
public class AppConfigServiceImpl implements AppConfigService {

    private final AppConfigRepo repo;

    @PostConstruct
    private void init() {
        getCloseConfirm();
        getAllowLogin();
    }

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
        return repo.findById(AppConstants.Config.CLOSE_CONFIRM_ID_INT)
                .orElseGet(() -> save(AppConfig.builder()
                        .id(AppConstants.Config.CLOSE_CONFIRM_ID_INT)
                        .name("close-confirm-duration")
                        .type(AppConfig.Type.NUMBER)
                        .classType(Integer.class.getCanonicalName())
                        .value(String.valueOf(30))
                        .description("Lama waktu yang diperlukan untuk menunggu requestor menjawab konfirmasi sebelum tiket close")
                        .build()));
    }

    @Override
    public AppConfig getAllowLogin() {
        return repo.findById(AppConstants.Config.ALLOW_OTHER_WITEL_ID_BOOL)
                .orElseGet(() -> save(AppConfig.builder()
                        .id(AppConstants.Config.ALLOW_OTHER_WITEL_ID_BOOL)
                        .name("allow-different-witel-login")
                        .type(AppConfig.Type.BOOLEAN)
                        .classType(Boolean.class.getCanonicalName())
                        .value(String.valueOf(false))
                        .description("Memperbolehkan user dengan Witel lain untuk login ke dashboard")
                        .build()));
    }

}
