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
    public AppConfig getCloseConfirm_int() {
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
    public AppConfig getAllowLogin_bool() {
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

    @Override
    public AppConfig getRegistrationRequireApproval_bool() {
        return repo.findById(AppConstants.Config.USER_REG_APPROVAL_ID_BOOL)
                .orElseGet(() -> save(AppConfig.builder()
                        .id(AppConstants.Config.USER_REG_APPROVAL_ID_BOOL)
                        .name("require-user-reg-approval")
                        .type(AppConfig.Type.BOOLEAN)
                        .classType(Boolean.class.getCanonicalName())
                        .value(String.valueOf(false))
                        .description("Registrasi user melalui bot telegram diperlukan approval dari admin")
                        .build()));
    }

    @Override
    public AppConfig getSendRegistrationApproval_bool() {
        return repo.findById(AppConstants.Config.SEND_REG_APPROVAL_TO_ADMINS_ID_BOOL)
                .orElseGet(() -> save(AppConfig.builder()
                        .id(AppConstants.Config.SEND_REG_APPROVAL_TO_ADMINS_ID_BOOL)
                        .name("notify-admins-user-reg-approval")
                        .type(AppConfig.Type.BOOLEAN)
                        .classType(Boolean.class.getCanonicalName())
                        .value(String.valueOf(false))
                        .description("Kirim notifikasi telegram untuk semua admin, ketika ada request approval registrasi user")
                        .build()));
    }

}
