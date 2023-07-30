package dev.scaraz.mars.core.service.credential.impl;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.request.CreateUserDTO;
import dev.scaraz.mars.common.domain.request.TelegramCreateUserDTO;
import dev.scaraz.mars.common.domain.request.UpdateUserDashboardDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.InternalServerException;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.AuthorityConstant;
import dev.scaraz.mars.common.utils.ConfigConstants;
import dev.scaraz.mars.core.config.datasource.AuditProvider;
import dev.scaraz.mars.core.config.event.app.AccountAccessEvent;
import dev.scaraz.mars.core.config.event.app.ConfigUpdateEvent;
import dev.scaraz.mars.core.domain.Config;
import dev.scaraz.mars.core.domain.cache.BotRegistration;
import dev.scaraz.mars.core.domain.cache.RegistrationApproval;
import dev.scaraz.mars.core.domain.credential.*;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.RoleQueryService;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
import dev.scaraz.mars.core.repository.cache.RegistrationApprovalRepo;
import dev.scaraz.mars.core.repository.db.credential.AccountRepo;
import dev.scaraz.mars.core.repository.db.credential.AccountSettingRepo;
import dev.scaraz.mars.core.repository.db.credential.RolesRepo;
import dev.scaraz.mars.core.service.ConfigService;
import dev.scaraz.mars.core.service.credential.AccountApprovalService;
import dev.scaraz.mars.core.service.credential.AccountService;
import dev.scaraz.mars.core.service.credential.RoleService;
import dev.scaraz.mars.core.util.DelegateUser;
import dev.scaraz.mars.security.MarsPasswordEncoder;
import dev.scaraz.mars.security.MarsUserContext;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static dev.scaraz.mars.common.utils.ConfigConstants.*;

@Slf4j
@RequiredArgsConstructor

@Service
public class AccountServiceImpl implements AccountService {

    private final MarsProperties marsProperties;
    //    private final AppConfigService appConfigService;
    private final ConfigService configService;
    private final AuditProvider auditProvider;
    private final TelegramBotService botService;


    private final AccountRepo accountRepo;
    private final AccountApprovalService accountApprovalService;
    private final RegistrationApprovalRepo registrationApprovalRepo;

    private final AccountQueryService accountQueryService;
    private final AccountSettingRepo settingRepo;

    private final RoleQueryService roleQueryService;
    private final RoleService roleService;

    private final MarsPasswordEncoder passwordEncoder;

    private final RolesRepo rolesRepo;

    @Override
    public Account save(Account account) {
        return accountRepo.save(account);
    }

    @Override
    public AccountSetting save(AccountSetting credential) {
        return settingRepo.save(credential);
    }

    @Override
    @Transactional
    public Account updatePassword(UserDetails user, String newPassword) {
        Account account;
        if (user instanceof Account) account = (Account) user;
        else account = accountQueryService.findById(((DelegateUser) user).getId());

        if (account.getCredentials().size() > 0) {
            for (AccountCredential credential : account.getCredentials()) {
                boolean matches = passwordEncoder.matches(newPassword, credential);
                if (matches) throw BadRequestException.args("Password baru tidak boleh sama dengan password lama");
            }
        }

        Map<String, Config> configs = configService.getBulkMap(
                CRD_PASSWORD_HISTORY_INT,
                CRD_PASSWORD_ALGO_STR,
                CRD_PASSWORD_HASH_ITERATION_INT,
                CRD_PASSWORD_SECRET_STR
        );

        AccountCredential newCredential = AccountCredential.builder()
                .account(account)
                .priority(10)
                .password(newPassword)
                .algorithm(configs.get(CRD_PASSWORD_ALGO_STR).getValue())
                .hashIteration(configs.get(CRD_PASSWORD_HASH_ITERATION_INT).getAsInt())
                .secret(configs.get(CRD_PASSWORD_SECRET_STR).getValue())
                .build();

        newCredential.setPassword(passwordEncoder.encode(newCredential));

        int historyLen = configs.get(CRD_PASSWORD_HISTORY_INT).getAsInt();
        if (historyLen <= 1) {
            log.debug("Clearing all previous password");
            account.getCredentials().clear();
        }
        else {
            log.debug("Updating all previous password");
            for (AccountCredential credential : account.getCredentials()) {
                credential.setPriority(credential.getPriority() + 10);
            }

            updateAccountCredentials(historyLen, account, true);
        }

        Set<AccountCredential> credentials = new LinkedHashSet<>();
        credentials.add(newCredential);
        credentials.addAll(account.getCredentials());
        account.setCredentials(credentials);
        try {
            return save(account);
        }
        finally {
            AccountAccessEvent.details("UPDATE_PASSWORD", account.getNik())
                    .publish();
        }
    }

    private boolean updateAccountCredentials(int historyLen, Account account, boolean updatePass) {
        int currentLen = account.getCredentials().size();
        if (currentLen >= historyLen) {
            ArrayList<AccountCredential> credentials = new ArrayList<>(account.getCredentials());
            int totalRemoval = currentLen - historyLen - (updatePass ? 1 : 0);

            if (totalRemoval > 0) {
                log.debug("Removing {} old password", totalRemoval);
                for (int i = 0; i < totalRemoval; i++) {
                    credentials.remove(credentials.size() - 1);
                }

                account.setCredentials(new LinkedHashSet<>(credentials));
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public Account create(CreateUserDTO req) {
        Account nuser = save(Account.builder()
                .name(req.getName())
                .nik(req.getNik())
                .phone(req.getPhone())
                .active(Optional.ofNullable(req.getActive()).orElse(false))
                .witel(req.getWitel())
                .sto(req.getSto())
                .email(req.getEmail())
                .tg(AccountTg.builder()
                        .username(req.getUsername())
                        .build())
//                .credential(UserCredential.builder()
//                        .email(req.getEmail())
//                        .username(req.getUsername())
//                        .build())
                .build());

        if (req.getRoles().size() > 0) {
            List<Role> roles = roleQueryService.findAll(RoleCriteria.builder()
                    .id(new StringFilter().setIn(req.getRoles()))
                    .build());
            roleService.addUserRoles(nuser, roles.toArray(new Role[0]));
        }
        else {
            Role roleUser = roleQueryService.findByIdOrName(AuthorityConstant.USER_ROLE);
            roleService.addUserRoles(nuser, roleUser);
        }

        nuser = save(nuser);
        log.info("SUCCESS CREATE USER -- ID {}", nuser.getId());
        return nuser;
    }

    @Transactional
    public void approval(String approvalId, boolean approved) {
        AccountApproval approval = accountApprovalService.findByIdOrNo(approvalId);

        if (approved) {
            Account nuser = save(Account.builder()
                    .name(approval.getName())
                    .nik(approval.getNik())
                    .phone(approval.getPhone())
                    .witel(approval.getWitel())
                    .sto(approval.getSto())
                    .tg(approval.getTg())
                    .active(true)
                    .build());

            Role roleUser = roleQueryService.findByIdOrName(AuthorityConstant.USER_ROLE);
            nuser.addRoles(roleUser);
            if (marsProperties.getWitel() == nuser.getWitel()) {
                Role roleAgent = roleQueryService.findByIdOrName(AuthorityConstant.AGENT_ROLE);
                nuser.addRoles(roleAgent);
            }

            try {
                botService.getClient().execute(SendMessage.builder()
                        .chatId(approval.getTg().getId())
                        .parseMode(ParseMode.MARKDOWNV2)
                        .text(TelegramUtil.esc(
                                "Request registrasi *" + approval.getNo() + "*, telah terkonfirmasi",
                                "",
                                "Selamat Datang di *MARS-ROC2*",
                                "untuk permulaan, ketik /help untuk melihat command yang tersedia"
                        ))
                        .build());
            }
            catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            accountApprovalService.delete(approvalId);
            save(nuser);

            AccountAccessEvent.details("WEB_ADMIN_APPROVAL", MarsUserContext.getUsername())
                    .put("approval_no", approval.getNo())
                    .put("approval_name", approval.getName())
                    .put("approval_nik", approval.getNik())
                    .put("message", "Approval request accepted")
                    .publish();
        }
        else {
            if (approval.getStatus().equals(AccountApproval.REQUIRE_DOCUMENT)) {
                accountApprovalService.delete(approvalId);

                try {
                    botService.getClient().execute(SendMessage.builder()
                            .chatId(approval.getTg().getId())
                            .parseMode(ParseMode.MARKDOWNV2)
                            .text(TelegramUtil.esc(
                                    "Maaf, request registrasi *" + approval.getNo() + "*, telah ditolak.",
                                    "",
                                    ""
                            ))
                            .build());
                }
                catch (TelegramApiException e) {
                    throw InternalServerException.args(e, "Unable to notify requestor");
                }
            }
            else {
                accountApprovalService.deleteCache(approvalId);
                approval.setStatus(AccountApproval.REQUIRE_DOCUMENT);
                try {
//                    List<String> emails = appConfigService.getApprovalAdminEmails_arr()
//                            .getAsArray();
                    List<String> emails = configService.get(ConfigConstants.ACC_REGISTRATION_EMAILS_LIST)
                            .getAsList();

                    String concatedEmails = emails.isEmpty() ? " - " : String.join("\n", emails);

                    botService.getClient().execute(SendMessage.builder()
                            .chatId(approval.getTg().getId())
                            .parseMode(ParseMode.MARKDOWNV2)
                            .text(TelegramUtil.esc(
                                    "Maaf, request registrasi *" + approval.getNo() + "*, telah ditolak.",
                                    "",
                                    "Silahkan menghubungi admin *MARS-ROC2* via email ke:",
                                    concatedEmails,
                                    "dengan melampirkan *KTP* dan *NDA* (Pakta Integritas) terbaru.",
                                    "",
                                    "Terima Kasih"
                            ))
                            .build());
                }
                catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                accountApprovalService.save(approval);
            }
        }
    }

    @Override
    public void pairing(Account account, BotRegistration registration) {
        account.setTg(AccountTg.builder()
                .id(registration.getId())
                .username(registration.getUsername())
                .build());

        if (!account.isActive())
            account.setActive(true);

        save(account);
    }

    @Override
    @Transactional
    public void createFromBot(boolean needApproval, TelegramCreateUserDTO req) {
        log.info("CREATE NEW USER FROM BOT -- REQUIRE APPROVAL={} DATA={}", needApproval, req);
        try {
            if (needApproval) {
                String regNo = "REG0" + req.getTgId();
                AccountApproval approval = accountApprovalService.save(AccountApproval.builder()
                        .no(regNo)
                        .status(AccountApproval.WAIT_APPROVAL)
                        .name(req.getName())
                        .nik(req.getNik())
                        .witel(req.getWitel())
                        .sto(req.getSto())
                        .tg(AccountTg.builder()
                                .id(req.getTgId())
                                .username(req.getTgUsername())
                                .build())
                        .phone(req.getPhone())
                        .build());

                registrationApprovalRepo.save(new RegistrationApproval(approval.getId(), 24));

//                Duration hourDuration = appConfigService.getApprovalDurationHour_drt()
//                        .getAsDuration();
                Duration hourDuration = configService.get(ConfigConstants.APP_USER_REGISTRATION_APPROVAL_DRT)
                        .getAsDuration();

                botService.getClient().execute(SendMessage.builder()
                        .chatId(req.getTgId())
                        .parseMode(ParseMode.MARKDOWNV2)
                        .text(TelegramUtil.esc(
                                "Registrasi *" + regNo + "*",
                                "Terima kasih, permintaan anda kami terima. Menunggu konfirmasi admin *MARS*",
                                "",
                                "_Jika dalam 1x" + hourDuration.toHours() + " jam belum terkonfirmasi, silahkan mengirim kembali registrasimu_"
                        ))
                        .build());
            }
            else {
                auditProvider.setName(req.getNik());
                Account account = accountRepo.saveAndFlush(Account.builder()
                        .nik(req.getNik())
                        .name(req.getName())
                        .phone(req.getPhone())
                        .active(true)
                        .witel(req.getWitel())
                        .sto(req.getSto())
                        .tg(AccountTg.builder()
                                .id(req.getTgId())
                                .username(req.getTgUsername())
                                .build())
                        .build());

                Role roleUser = roleQueryService.findByIdOrName(AuthorityConstant.USER_ROLE);
                account.addRoles(roleUser);
                if (marsProperties.getWitel() == account.getWitel()) {
                    Role roleAgent = roleQueryService.findByIdOrName(AuthorityConstant.AGENT_ROLE);
                    account.addRoles(roleAgent);
                }

                save(account);
            }
        }
        catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        finally {
            auditProvider.clear();
        }
    }

    @Override
    @Transactional
    public Account updatePartial(String userId, UpdateUserDashboardDTO dto) {
        log.info("PARTIAL DATA USER UPDATE {}", dto);
        Account account = accountQueryService.findById(userId);

        boolean selfUpdate = MarsUserContext.getId().equals(account.getId());
        String accessType = selfUpdate ? "WEB_UPDATE_PROFILE" : "WEB_ADMIN_UPDATE_PROFILE";

        if (dto.getNik() != null) account.setNik(dto.getNik());
        if (dto.getPhone() != null) account.setPhone(dto.getPhone());
        if (dto.getActive() != null) account.setActive(dto.getActive());

        if (dto.getWitel() != null && account.getWitel() != dto.getWitel()) {
            log.debug("UPDATE USER WITEL TO {} FROM {}", dto.getWitel(), account.getWitel());
            account.setWitel(dto.getWitel());
        }

        if (dto.getSto() != null) account.setSto(dto.getSto());
        if (dto.getEmail() != null) account.setEmail(dto.getEmail());

        if (dto.getTg() != null) {
            UpdateUserDashboardDTO.UpdateTelegram tg = dto.getTg();

            if (!Objects.equals(account.getTg().getUsername(), tg.getUsername()))
                account.getTg().setUsername(tg.getUsername());
        }

        if (dto.getRoles() != null) {
            if (dto.getRoles().isEmpty())
                throw BadRequestException.args("sebuah akun setidaknya harus memiliki 1 role");

            log.debug("DELETING ALL USER ROLE: {}", userId);
            rolesRepo.deleteAllByAccountId(userId);
            rolesRepo.saveAll(roleQueryService.findAllByNames(dto.getRoles()).stream()
                    .map(r -> new Roles(account, r))
                    .collect(Collectors.toList()));
        }

        if (!selfUpdate) {
            AccountAccessEvent.details(accessType, MarsUserContext.getUsername())
                    .put("acc_id", account.getId())
                    .put("acc_nik", account.getNik())
                    .publish();
        }

        return save(account);
    }

    @Transactional
    @EventListener(ConfigUpdateEvent.class)
    public void onAppConfigurationUpdate(ConfigUpdateEvent event) {
        if (event.is(CRD_PASSWORD_HISTORY_INT) && event.isValueChanged()) {
            Config config = event.getConfig();
            int length = config.getAsInt();

            List<Account> all = accountQueryService.findAll();
            for (Account account : all) {
                boolean updated = updateAccountCredentials(length, account, false);
                if (updated) save(account);
            }
        }
    }

}
