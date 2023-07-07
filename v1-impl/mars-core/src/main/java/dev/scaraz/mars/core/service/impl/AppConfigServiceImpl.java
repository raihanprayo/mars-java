package dev.scaraz.mars.core.service.impl;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.domain.AppConfig;
import dev.scaraz.mars.core.repository.AppConfigRepo;
import dev.scaraz.mars.core.service.AppConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Duration;
import java.util.List;

import static dev.scaraz.mars.common.utils.AppConstants.Config.*;

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
    public AppConfig create(long id, String name, Object value, @Nullable String description) {
        if (value instanceof String) return create(id, name, (String) value, description);
        if (value instanceof Number) return create(id, name, (Number) value, description);
        if (value instanceof Boolean) return create(id, name, (Boolean) value, description);
        if (value instanceof Iterable) return create(id, name, (Iterable<String>) value, description);
        if (value instanceof Duration) return create(id, name, (Duration) value, description);

        if (value instanceof Serializable) {
            AppConfig appConfig = createNew(id, name, description);
            appConfig.setAsJson((Serializable) value);
            return save(appConfig);
        }

        throw BadRequestException.args("Unable to determine config type");
    }

    @Override
    public AppConfig create(long id, String name, String value, @Nullable String description) {
        AppConfig appConfig = createNew(id, name, description);
        appConfig.setAsString(value);
        return save(appConfig);
    }

    @Override
    public <T extends Number> AppConfig create(long id, String name, T value, @Nullable String description) {
        AppConfig appConfig = createNew(id, name, description);
        appConfig.setAsNumber(value);
        return save(appConfig);
    }

    @Override
    public AppConfig create(long id, String name, Boolean value, @Nullable String description) {
        AppConfig appConfig = createNew(id, name, description);
        appConfig.setAsBoolean(value);
        return save(appConfig);
    }

    @Override
    public AppConfig create(long id, String name, Iterable<String> values, @Nullable String description) {
        AppConfig appConfig = createNew(id, name, description);
        appConfig.setAsArray(values);
        return save(appConfig);
    }

    @Override
    public AppConfig create(long id, String name, Duration duration, @Nullable String description) {
        AppConfig appConfig = createNew(id, name, description);
        appConfig.setAsDuration(duration);
        return save(appConfig);
    }

    @Override
    public AppConfig getById(long id) {
        return repo.findById(id)
                .orElseThrow();
    }


    @Override
    public AppConfig getCloseConfirm_drt() {
        return repo.findById(CLOSE_CONFIRM_ID_DRT)
                .orElseGet(() -> create(CLOSE_CONFIRM_ID_DRT,
                        "close-confirm-duration",
                        Duration.ofMinutes(30),
                        "Lama waktu yang diperlukan untuk menunggu requestor menjawab konfirmasi sebelum tiket close"
                ));
    }

    @Override
    public AppConfig getAllowLogin_bool() {
        return repo.findById(AppConstants.Config.ALLOW_OTHER_WITEL_ID_BOOL)
                .orElseGet(() -> create(ALLOW_OTHER_WITEL_ID_BOOL,
                        "allow-different-witel-login",
                        false,
                        "Memperbolehkan user dengan Witel lain untuk login ke dashboard"
                ));
    }

    @Override
    public AppConfig getRegistrationRequireApproval_bool() {
        return repo.findById(AppConstants.Config.USER_REG_APPROVAL_ID_BOOL)
                .orElseGet(() -> create(USER_REG_APPROVAL_ID_BOOL,
                        "require-user-reg-approval",
                        false,
                        "Registrasi user melalui bot telegram diperlukan approval dari admin"
                ));
    }

    @Override
    public AppConfig getSendRegistrationApproval_bool() {
        return repo.findById(AppConstants.Config.SEND_REG_APPROVAL_TO_ADMINS_ID_BOOL)
                .orElseGet(() -> create(SEND_REG_APPROVAL_TO_ADMINS_ID_BOOL,
                        "notify-admins-user-reg-approval",
                        false,
                        "Kirim notifikasi telegram untuk semua admin, ketika ada request approval registrasi user"
                ));
    }

    @Override
    public AppConfig getPostPending_drt() {
        return repo.findById(AppConstants.Config.POST_PENDING_CONFIRM_ID_DRT)
                .orElseGet(() -> create(POST_PENDING_CONFIRM_ID_DRT,
                        "post-pending-confirm-duration",
                        Duration.ofMinutes(60),
                        "Lama waktu yang diperlukan untuk menunggu tiket dengan status pending"
                ));
    }

    @Override
    public AppConfig getApprovalDurationHour_drt() {
        return repo.findById(AppConstants.Config.USER_REG_APPROVAL_DURATION_ID_DRT)
                .orElseGet(() -> create(USER_REG_APPROVAL_DURATION_ID_DRT,
                        "reg-approval-duration",
                        Duration.ofHours(1),
                        "Lama waktu yang diperlukan untuk menunggu approval registrasi"
                ));
    }

    @Override
    public AppConfig getApprovalAdminEmails_arr() {
        return repo.findById(APPROVAL_ADMIN_EMAILS_ID_ARR)
                .orElseGet(() -> create(APPROVAL_ADMIN_EMAILS_ID_ARR,
                        "reg-approval-admin-emails",
                        List.of(),
                        "Email admin yang dikirim untuk keperluan registrasi"
                ));
    }

    @Override
    public AppConfig getAllowAgentCreateTicket_bool() {
        return repo.findById(ALLOW_AGENT_CREATE_TICKET_BOOL)
                .orElseGet(() -> create(ALLOW_AGENT_CREATE_TICKET_BOOL,
                        "allow-agent-create-ticket",
                        false,
                        "Agent diperbolehkan membuat tiket sendiri"
                ));
    }

    @Override
    public AppConfig getTelegramStartIssueColumn_int() {
        return repo.findById(TELEGRAM_ISSUE_COLUMN_INT)
                .orElseGet(() -> create(TELEGRAM_ISSUE_COLUMN_INT,
                        "telegram-cmd-start-issue-column",
                        3,
                        "Menyesuaikan jumlah kolom pada command /start"));
    }

    private AppConfig createNew(long id, String name, @Nullable String description) {
        AppConfig appConfig = new AppConfig();
        appConfig.setId(id);
        appConfig.setName(name);
        appConfig.setTitle(title(id));
        appConfig.setDescription(description);
        return appConfig;
    }
}
