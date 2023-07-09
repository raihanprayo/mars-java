package dev.scaraz.mars.core.service.credential.impl;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.request.CreateUserDTO;
import dev.scaraz.mars.common.domain.request.TelegramCreateUserDTO;
import dev.scaraz.mars.common.domain.request.UpdateUserDashboardDTO;
import dev.scaraz.mars.common.exception.web.InternalServerException;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.common.utils.ConfigConstants;
import dev.scaraz.mars.core.config.datasource.AuditProvider;
import dev.scaraz.mars.core.domain.cache.BotRegistration;
import dev.scaraz.mars.core.domain.cache.RegistrationApproval;
import dev.scaraz.mars.core.domain.credential.*;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.RoleQueryService;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
import dev.scaraz.mars.core.repository.cache.RegistrationApprovalRepo;
import dev.scaraz.mars.core.repository.db.credential.RolesRepo;
import dev.scaraz.mars.core.repository.db.credential.AccountRepo;
import dev.scaraz.mars.core.repository.db.credential.AccountSettingRepo;
import dev.scaraz.mars.core.service.AppConfigService;
import dev.scaraz.mars.core.service.ConfigService;
import dev.scaraz.mars.core.service.credential.AccountService;
import dev.scaraz.mars.core.service.credential.RoleService;
import dev.scaraz.mars.core.service.credential.AccountApprovalService;
import dev.scaraz.mars.core.util.DelegateUser;
import dev.scaraz.mars.security.MarsPasswordEncoder;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.util.*;

@Slf4j
@RequiredArgsConstructor

@Service
public class AccountServiceImpl implements AccountService {

    private final MarsProperties marsProperties;
    private final AppConfigService appConfigService;
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

        String algo = configService.get(ConfigConstants.CRD_DEFAULT_PASSWORD_ALGO_STR).getValue();
        int hashIteration = configService.get(ConfigConstants.CRD_DEFAULT_PASSWORD_ITERATION_INT).getAsInt();
        String secret = configService.get(ConfigConstants.CRD_DEFAULT_PASSWORD_SECRET_STR).getValue();

        AccountCredential credential = AccountCredential.builder()
                .account(account)
                .priority(10)
                .password(newPassword)
                .hashIteration(hashIteration)
                .algorithm(algo)
                .secret(secret)
                .build();

        credential.setPassword(passwordEncoder.encode(credential));
        account.getCredentials().clear();
        account.getCredentials().add(credential);
        return save(account);
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
            Role roleUser = roleQueryService.findByIdOrName(AppConstants.Authority.USER_ROLE);
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

            Role roleUser = roleQueryService.findByIdOrName(AppConstants.Authority.USER_ROLE);
            nuser.addRoles(roleUser);
            if (marsProperties.getWitel() == nuser.getWitel()) {
                Role roleAgent = roleQueryService.findByIdOrName(AppConstants.Authority.AGENT_ROLE);
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
                    List<String> emails = appConfigService.getApprovalAdminEmails_arr()
                            .getAsArray();

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

                Duration hourDuration = appConfigService.getApprovalDurationHour_drt()
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

                Role roleUser = roleQueryService.findByIdOrName(AppConstants.Authority.USER_ROLE);
                account.addRoles(roleUser);
                if (marsProperties.getWitel() == account.getWitel()) {
                    Role roleAgent = roleQueryService.findByIdOrName(AppConstants.Authority.AGENT_ROLE);
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

        if (dto.getNik() != null) account.setNik(dto.getNik());
        if (dto.getPhone() != null) account.setPhone(dto.getPhone());
        if (dto.getActive() != null) account.setActive(dto.getActive());
        if (dto.getWitel() != null) account.setWitel(dto.getWitel());
        if (dto.getSto() != null) account.setSto(dto.getSto());
        if (dto.getEmail() != null) account.setEmail(dto.getEmail());

        if (dto.getTg() != null) {
            UpdateUserDashboardDTO.UpdateTelegram tg = dto.getTg();

            if (!Objects.equals(account.getTg().getUsername(), tg.getUsername()))
                account.getTg().setUsername(tg.getUsername());
        }

        if (dto.getRoles() != null) {
            List<String> removedIds = dto.getRoles().getRemoved();
            List<String> selectedIds = dto.getRoles().getSelected();

            rolesRepo.deleteByAccountIdAndRoleIdIn(userId, removedIds);

            List<Role> roles = new ArrayList<>();
            for (String roleId : selectedIds) {
                Role role = roleQueryService.findByIdOrName(roleId);
                if (rolesRepo.existsByAccountIdAndRoleName(userId, role.getName())) {
                    roles.add(role);
                    continue;
                }

                rolesRepo.save(Roles.builder()
                        .account(account)
                        .role(role)
                        .build());
                roles.add(role);
            }

            account.setRoles(new LinkedHashSet<>(roles));
        }

        return save(account);
    }

}
