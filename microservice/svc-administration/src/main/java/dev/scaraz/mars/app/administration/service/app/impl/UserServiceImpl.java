package dev.scaraz.mars.app.administration.service.app.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.scaraz.mars.app.administration.config.CacheConfiguration;
import dev.scaraz.mars.app.administration.domain.cache.FormUserRegistrationCache;
import dev.scaraz.mars.app.administration.domain.db.Config;
import dev.scaraz.mars.app.administration.domain.db.UserApproval;
import dev.scaraz.mars.app.administration.service.RealmService;
import dev.scaraz.mars.app.administration.service.app.ConfigService;
import dev.scaraz.mars.app.administration.service.app.UserApprovalService;
import dev.scaraz.mars.app.administration.service.app.UserService;
import dev.scaraz.mars.app.administration.service.query.UserApprovalQueryService;
import dev.scaraz.mars.app.administration.web.dto.UserRegistrationDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.MarsException;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserPoliciesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.authorization.UserPolicyRepresentation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserServiceImpl implements UserService {

    private final RealmResource realmResource;
    private final ConfigService configService;

    //    private final UserApprovalRepo userApprovalRepo;
    private final UserApprovalService userApprovalService;
    private final UserApprovalQueryService userApprovalQueryService;
    private final TelegramBotService telegramBotService;

    @Lazy
    private final RealmService realmService;
    private final ObjectMapper objectMapper;


    @Cacheable(
            cacheNames = CacheConfiguration.CACHE_KEYCLOAK_USER,
            unless = "#result == null",
            key = "#telegramId",
            sync = true)
    @Override
    public UserRepresentation findByTelegramId(long telegramId) throws IllegalStateException {
        List<UserRepresentation> users = realmResource.users().searchByAttributes("telegram:" + telegramId);
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
        return realmResource.users().get(id).toRepresentation();
    }

    @Override
    public UserRepresentation createUser(UserRegistrationDTO dto) {
        log.info("ADD NEW user -- {}/{}/{}", dto.getTelegramId(), dto.getWitel(), dto.getNik());

        UsersResource users = realmResource.users();
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(dto.getNik());

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
        try (Response response = users.create(user)) {
            log.info("CREATE USER RESPONSE STATUS -- {}", response.getStatus());

            if (response.getStatus() == 201) {
                String createdId = CreatedResponseUtil.getCreatedId(response);
                return addImpersonatedPolicy(users.get(createdId).toRepresentation());
            }

            Object entity = response.getEntity();
            if (response.getEntity() instanceof InputStream) {
                try {
                    String content = new String(((InputStream) entity).readAllBytes(), StandardCharsets.UTF_8);
                    log.warn(content);

                    throw BadRequestException
                            .args("Failed to create user account")
                            .setRef(objectMapper.readValue((InputStream) entity, new TypeReference<Map<String, Object>>() {
                            }));
                }
                catch (Exception e) {
                    if (e instanceof MarsException)
                        throw (MarsException) e;
                }
            }

            log.warn("CREATE USER Response Entity -- {}", entity);
            throw BadRequestException.args("Failed to create user account");
        }
    }

    @Override
    public RegistrationResult createUserFromBot(FormUserRegistrationCache cache) {
        // TODO: config user approval
        boolean needApproval = configService.get(Config.USER_REGISTRATION_APPROVAL_BOOL).getAsBoolean();
        Duration approvalDuration = configService.get(Config.USER_REGISTRATION_APPROVAL_DRT).getAsDuration();

        if (needApproval) {
            log.info("ADD NEW user-registration-approval -- {}/{}/{}", cache.getId(), cache.getWitel(), cache.getNik());
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

            return new RegistrationResult(true, registration.getNo(), approvalDuration);
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

            return new RegistrationResult(false, null, null);
        }
    }

    @Override
    @Transactional
    public RegistrationResult createUserFromApproval(String approvalNoOrId, boolean approved) {
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
            return new RegistrationResult(false, registration.getNo(), null);
        }
        else {
            if (registration.getStatus().equals(UserApproval.REQUIRE_DOCUMENT)) {
                deleteRegistration(registration);
                return new RegistrationResult(true, registration.getNo(), null);
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
                    return new RegistrationResult(true, registration.getNo(), null);
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

    private UserRepresentation addImpersonatedPolicy(UserRepresentation user) {
        try {
            UserPoliciesResource userPoliciesResource = realmService.getRealmManagementClientResource().authorization().policies().user();
            UserPolicyRepresentation policy = userPoliciesResource.findByName(RealmService.USER_IMPERSONATED);
            policy.addUser(user.getId());
            userPoliciesResource.findById(policy.getId()).update(policy);

            log.info("UPDATE USER POLICY");
        }
        catch (Exception ex) {
        }
        return user;
    }

}
