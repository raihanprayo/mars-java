package dev.scaraz.mars.app.administration.service.app.impl;

import dev.scaraz.mars.app.administration.domain.cache.FormRegistrationCache;
import dev.scaraz.mars.app.administration.domain.db.Config;
import dev.scaraz.mars.app.administration.domain.db.UserApproval;
import dev.scaraz.mars.app.administration.service.app.ConfigService;
import dev.scaraz.mars.app.administration.service.app.UserApprovalService;
import dev.scaraz.mars.app.administration.service.app.UserService;
import dev.scaraz.mars.app.administration.service.query.UserApprovalQueryService;
import dev.scaraz.mars.app.administration.web.dto.UserRegistrationDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserServiceImpl implements UserService {

    private final RealmResource realm;
    private final ConfigService configService;

    //    private final UserApprovalRepo userApprovalRepo;
    private final UserApprovalService userApprovalService;
    private final UserApprovalQueryService userApprovalQueryService;
    private final TelegramBotService telegramBotService;

    @Cacheable(
            cacheNames = "tg:user",
            unless = "#result == null",
            sync = true)
    @Override
    public UserRepresentation findByTelegramId(long telegramId) throws IllegalStateException {
        List<UserRepresentation> users = realm.users().searchByAttributes("telegram:" + telegramId);
        if (users.size() != 1) throw new IllegalStateException("user not found");
        return users.get(0);
    }

    @Override
    public Optional<UserRepresentation> findByTelegramIdOpt(long telegramId) {
        try {
            UserRepresentation user = findByTelegramId(telegramId);
            return Optional.of(user);
        }
        catch (IllegalStateException ex) {
        }
        return Optional.empty();
    }

    @Override
    public UserRepresentation findById(String id) {
        return realm.users().get(id).toRepresentation();
    }

    @Override
    public UserRepresentation createUser(UserRegistrationDTO dto) {
        UsersResource users = realm.users();
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        String name = dto.getName().trim();
        String[] split = name.split(" ");

        if (split.length == 1) user.setFirstName(name);
        else {
            int i = name.lastIndexOf(" ");
            user.setFirstName(name.substring(0, i).trim());
            user.setLastName(name.substring(i).trim());
        }

        MultiValueMap<String, String> attributes = new LinkedMultiValueMap<>();
        attributes.set("phone", dto.getPhone());
        attributes.set("witel", dto.getWitel().name());

        if (StringUtils.isNotBlank(dto.getSto()))
            attributes.set("sto", dto.getSto());

        if (dto.getTelegramId() != null)
            attributes.set("telegram", dto.getTelegramId().toString());

        user.setAttributes(attributes);
        String createdId = CreatedResponseUtil.getCreatedId(users.create(user));
        return users.get(createdId).toRepresentation();
    }

    @Override
    public BotRegistrationResult createUserFromBot(FormRegistrationCache cache) {
        // TODO: config user approval
        boolean needApproval = configService.get(Config.USER_REGISTRATION_APPROVAL_BOOL).getAsBoolean();
        Duration approvalDuration = configService.get(Config.USER_REGISTRATION_APPROVAL_DRT).getAsDuration();

        if (needApproval) {
            String no = "REG" + cache.getId();

            UserApproval registration = userApprovalService.save(UserApproval.builder()
                    .no(no)
                    .telegramId(cache.getId())
                    .status(UserApproval.WAIT_APPROVAL)
                    .name(cache.getName())
                    .nik(cache.getNik())
                    .phone(cache.getPhone())
                    .witel(cache.getWitel())
                    .sto(cache.getSto())
                    .build());

            return new BotRegistrationResult(true, registration.getNo(), approvalDuration);
        }
        else {
            createUser(UserRegistrationDTO.builder()
                    .name(cache.getName())
                    .nik(cache.getNik())
                    .phone(cache.getPhone())
                    .witel(cache.getWitel())
                    .sto(cache.getSto())
                    .telegramId(cache.getId())
                    .build());

            return new BotRegistrationResult(false, null, null);
        }
    }

    @Override
    public void createUserFromApproval(String approvalNoOrId, boolean approved) {
        UserApproval registration = userApprovalQueryService.findByIdOrNo(approvalNoOrId);

        if (approved) {
            createUser(UserRegistrationDTO.builder()
                    .name(registration.getName())
                    .nik(registration.getNik())
                    .phone(registration.getPhone())
                    .witel(registration.getWitel())
                    .sto(registration.getSto())
                    .telegramId(registration.getTelegramId())
                    .build());

            userApprovalService.deleteById(registration.getId());
        }
        else {
            if (registration.getStatus().equals(UserApproval.REQUIRE_DOCUMENT)) {
                deleteRegistration(registration);
            }
            else {
                registration.setStatus(UserApproval.REQUIRE_DOCUMENT);
                List<String> emails = configService.get(Config.USER_REGISTRATION_EMAIL_LIST).getAsList();
                String concatedEmails = emails.isEmpty() ? " - " : String.join("\n", emails);
                try {
                    telegramBotService.getClient().execute(SendMessage.builder()
                            .chatId(registration.getTelegramId())
                            .text(TelegramUtil.esc(
                                    "Maaf, request registrasi *" + registration.getNo() + "*, telah ditolak.",
                                    "",
                                    "Silahkan menghubungi admin *MARS-ROC2* via email ke:",
                                    concatedEmails,
                                    "dengan melampirkan *KTP* dan *NDA* (Pakta Integritas) terbaru.",
                                    "",
                                    "Terima Kasih"
                            ))
                            .build());
                    userApprovalService.save(registration);
                }
                catch (TelegramApiException e) {
                    throw BadRequestException.args("Unable to notify requestor: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void deleteRegistration(String approvalNoOrId) {
        UserApproval registration = userApprovalQueryService.findByIdOrNo(approvalNoOrId);
        deleteRegistration(registration);
    }

    private void deleteRegistration(UserApproval registration) {
        log.info("DELETE USER APPROVAL - {}", registration.getId());
        try {
            telegramBotService.getClient().execute(SendMessage.builder()
                    .chatId(registration.getTelegramId())
                    .text(TelegramUtil.esc("Maaf, request registrasimu dengan nomor *" + registration.getNo() + "*, telah ditolak."))
                    .build());
            userApprovalService.deleteById(registration.getId());
        }
        catch (TelegramApiException e) {
            throw BadRequestException.args("Unable to notify requestor: " + e.getMessage());
        }
    }

}
