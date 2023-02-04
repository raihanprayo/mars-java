package dev.scaraz.mars.core.service.credential.impl;

import dev.scaraz.mars.common.domain.request.CreateUserDTO;
import dev.scaraz.mars.common.domain.request.TelegramCreateUserDTO;
import dev.scaraz.mars.common.domain.request.UserUpdateDashboardDTO;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.config.datasource.AuditProvider;
import dev.scaraz.mars.core.domain.cache.BotRegistration;
import dev.scaraz.mars.core.domain.cache.RegistrationApproval;
import dev.scaraz.mars.core.domain.credential.*;
import dev.scaraz.mars.core.query.GroupQueryService;
import dev.scaraz.mars.core.query.RoleQueryService;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
import dev.scaraz.mars.core.repository.cache.RegistrationApprovalRepo;
import dev.scaraz.mars.core.repository.credential.*;
import dev.scaraz.mars.core.service.AppConfigService;
import dev.scaraz.mars.core.service.credential.RoleService;
import dev.scaraz.mars.core.service.credential.UserApprovalService;
import dev.scaraz.mars.core.service.credential.UserService;
import dev.scaraz.mars.core.util.DelegateUser;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserServiceImpl implements UserService {

    private final AppConfigService appConfigService;
    private final AuditProvider auditProvider;
    private final TelegramBotService botService;


    private final UserRepo userRepo;
    private final UserApprovalService userApprovalService;
    private final RegistrationApprovalRepo registrationApprovalRepo;

    private final UserQueryService userQueryService;
    private final UserSettingRepo settingRepo;

    private final RoleQueryService roleQueryService;
    private final RoleService roleService;

    private final GroupQueryService groupQueryService;

    private final PasswordEncoder passwordEncoder;

    private final RolesRepo rolesRepo;

    @Override
    public User save(User user) {
        return userRepo.save(user);
    }

    @Override
    public UserSetting save(UserSetting credential) {
        return settingRepo.save(credential);
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        User account;
        if (user instanceof User) account = (User) user;
        else {
            account = userQueryService.findById(((DelegateUser) user).getId());
        }

        account.setPassword(passwordEncoder.encode(newPassword));
        return save(account);
    }

    @Override
    @Transactional
    public User create(CreateUserDTO req) {
        User nuser = save(User.builder()
                .name(req.getName())
                .nik(req.getNik())
                .phone(req.getPhone())
                .active(Optional.ofNullable(req.getActive()).orElse(false))
                .witel(req.getWitel())
                .sto(req.getSto())
                .email(req.getEmail())
                .tg(UserTg.builder()
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

        if (req.getGroup() != null) {
            Group group = groupQueryService.findByIdOrName(req.getGroup());
            nuser.setGroup(group);
        }

        nuser = save(nuser);
        log.info("SUCCESS CREATE USER -- ID {}", nuser.getId());
        return nuser;
    }

    @Transactional
    public void approval(String approvalId, boolean approved) {
        UserApproval approval = userApprovalService.findByIdOrNo(approvalId);

        if (approved) {
            User nuser = save(User.builder()
                    .name(approval.getName())
                    .nik(approval.getNik())
                    .phone(approval.getPhone())
                    .witel(approval.getWitel())
                    .sto(approval.getSto())
                    .tg(approval.getTg())
                    .active(true)
                    .build());

            Role roleUser = roleQueryService.findByIdOrName(AppConstants.Authority.USER_ROLE);
            nuser.setRoles(Set.of(roleUser));

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
            userApprovalService.delete(approvalId);
            save(nuser);
        }
        else {
            approval.setStatus(UserApproval.REQUIRE_DOCUMENT);
            try {
                List<String> emails = appConfigService.getApprovalAdminEmails_arr()
                        .getAsArray();

                String concatedEmails = emails.isEmpty() ? " - " : String.join("\n", emails);

                botService.getClient().execute(SendMessage.builder()
                        .chatId(approval.getTg().getId())
                        .parseMode(ParseMode.MARKDOWNV2)
                        .text(TelegramUtil.esc(
                                "Request registrasi *" + approval.getNo() + "*, telah ditolak.",
                                "",
                                "Silahkan menghubungi admin *MARS-ROC2* via email ke:",
                                concatedEmails,
                                "dengan melampirkan KTP dan NDA (Pakta Integritas) terbaru.",
                                "",
                                "Terima Kasih"
                        ))
                        .build());
            }
            catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            userApprovalService.save(approval);
        }
        userApprovalService.deleteCache(approvalId);
    }

    @Override
    public void pairing(User user, BotRegistration registration) {
        user.setTg(UserTg.builder()
                .id(registration.getId())
                .username(registration.getUsername())
                .build());

        if (!user.isActive())
            user.setActive(true);

        save(user);
    }

    @Override
    @Transactional
    public void createFromBot(@Nullable Group group, boolean needApproval, TelegramCreateUserDTO req) {
        log.info("CREATE NEW USER FROM BOT -- REQUIRE APPROVAL={} DATA={}", needApproval, req);
        try {
            if (needApproval) {
                String regNo = "REG0" + req.getTgId();
                UserApproval approval = userApprovalService.save(UserApproval.builder()
                        .no(regNo)
                        .status(UserApproval.WAIT_APPROVAL)
                        .name(req.getName())
                        .nik(req.getNik())
                        .witel(req.getWitel())
                        .sto(req.getSto())
                        .tg(UserTg.builder()
                                .id(req.getTgId())
                                .username(req.getTgUsername())
                                .build())
                        .phone(req.getPhone())
                        .build());

                registrationApprovalRepo.save(new RegistrationApproval(approval.getId(), 24));

                botService.getClient().execute(SendMessage.builder()
                        .chatId(req.getTgId())
                        .parseMode(ParseMode.MARKDOWNV2)
                        .text(TelegramUtil.esc(
                                "Registrasi *" + regNo + "*",
                                "Terima kasih, permintaan anda kami terima. Menunggu konfirmasi admin *MARS*",
                                "",
                                "_Jika dalam 1x24 jam tidak jawaban,",
                                "silahkan mengirim kembali registrasimu_"
                        ))
                        .build());
            }
            else {
                auditProvider.setName(req.getNik());
                User user = userRepo.saveAndFlush(User.builder()
                        .nik(req.getNik())
                        .name(req.getName())
                        .phone(req.getPhone())
                        .active(true)
                        .witel(req.getWitel())
                        .sto(req.getSto())
                        .tg(UserTg.builder()
                                .id(req.getTgId())
                                .username(req.getTgUsername())
                                .build())
                        .build());

                Role roleUser = roleQueryService.findByIdOrName("user");
                user.addRoles(roleUser);

                save(user);
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
    public User updatePartial(String userId, UserUpdateDashboardDTO dto) {
        log.info("PARTIAL DATA USER UPDATE {}", dto);
        User user = userQueryService.findById(userId);

        if (dto.getNik() != null) user.setNik(dto.getNik());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getActive() != null) user.setActive(dto.getActive());
        if (dto.getWitel() != null) user.setWitel(dto.getWitel());
        if (dto.getSto() != null) user.setSto(dto.getSto());


        if (dto.getRoles() != null) {
            List<String> removedIds = dto.getRoles().getRemoved();
            List<String> selectedIds = dto.getRoles().getSelected();

            rolesRepo.deleteByUserIdAndRoleIdIn(userId, removedIds);

            Set<Role> roles = new HashSet<>();
            for (String roleId : selectedIds) {
                Role role = roleQueryService.findByIdOrName(roleId);
                if (rolesRepo.existsByUserIdAndRoleName(userId, role.getName())) {
                    roles.add(role);
                    continue;
                }

                rolesRepo.save(Roles.builder()
                        .user(user)
                        .role(role)
                        .build());
                roles.add(role);
            }

            user.setRoles(roles);
        }

        return save(user);
    }

}
