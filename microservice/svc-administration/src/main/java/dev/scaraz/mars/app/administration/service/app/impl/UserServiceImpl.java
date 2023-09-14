package dev.scaraz.mars.app.administration.service.app.impl;

import dev.scaraz.mars.app.administration.domain.cache.UserRegistrationCache;
import dev.scaraz.mars.app.administration.domain.db.Config;
import dev.scaraz.mars.app.administration.domain.db.UserRegistration;
import dev.scaraz.mars.app.administration.repository.db.UserRegistrationRepo;
import dev.scaraz.mars.app.administration.service.app.ConfigService;
import dev.scaraz.mars.app.administration.service.app.UserService;
import dev.scaraz.mars.app.administration.web.dto.UserRegistrationDTO;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
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

    private final UserRegistrationRepo userRegistrationRepo;
    private final TelegramBotService telegramBotService;

    private final StringRedisTemplate stringRedisTemplate;

    @Cacheable(
            cacheNames = "tg:user",
            unless = "#result == null",
            sync = true)
    @Override
    public UserRepresentation findByTelegramId(long telegramId) throws IllegalStateException {
        List<UserRepresentation> users = realm.users().searchByAttributes("telegram=" + telegramId);
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
    public void registerNewUser(UserRegistrationDTO dto) {
        UsersResource users = realm.users();
        UserRepresentation user = new UserRepresentation();

        // TODO: config user approval
        boolean needApproval = configService.get(Config.USER_REGISTRATION_APPROVAL_BOOL).getAsBoolean();
        Duration approvalDuration = configService.get(Config.USER_REGISTRATION_APPROVAL_DRT).getAsDuration();

        if (needApproval) {
            String no = null;
            if (dto.getTelegramId() != null)
                no = "REG" + dto.getTelegramId();

            UserRegistration registration = userRegistrationRepo.save(UserRegistration.builder()
                    .no(no)
                    .telegramId(dto.getTelegramId())
                    .status(UserRegistration.WAIT_APPROVAL)
                    .name(dto.getName())
                    .nik(dto.getNik())
                    .phone(dto.getPhone())
                    .witel(dto.getWitel())
                    .sto(dto.getSto())
                    .build());

            if (TelegramContextHolder.hasContext()) {
                try {
                    telegramBotService.getClient().execute(SendMessage.builder()
                            .chatId(dto.getTelegramId())
                            .parseMode(ParseMode.MARKDOWNV2)
                            .text(TelegramUtil.esc(
                                    "Registrasi *" + registration.getNo() + "*",
                                    "Terima kasih, permintaan anda kami terima. Menunggu konfirmasi admin *MARS*",
                                    "",
                                    "_Jika dalam 1x" + approvalDuration.toHours() + " jam belum terkonfirmasi, silahkan mengirim kembali registrasimu_"
                            ))
                            .build());
                }
                catch (TelegramApiException e) {
                }
            }
        }
        else {
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
            users.create(user);
        }
    }

    @Override
    public BotRegistrationResult registerFromBot(UserRegistrationCache cache) {
        UsersResource users = realm.users();
        UserRepresentation user = new UserRepresentation();

        // TODO: config user approval
        boolean needApproval = configService.get(Config.USER_REGISTRATION_APPROVAL_BOOL).getAsBoolean();
        Duration approvalDuration = configService.get(Config.USER_REGISTRATION_APPROVAL_DRT).getAsDuration();

        if (needApproval) {
            String no = "REG" + cache.getId();

            UserRegistration registration = userRegistrationRepo.save(UserRegistration.builder()
                    .no(no)
                    .telegramId(cache.getId())
                    .status(UserRegistration.WAIT_APPROVAL)
                    .name(cache.getName())
                    .nik(cache.getNik())
                    .phone(cache.getPhone())
                    .witel(cache.getWitel())
                    .sto(cache.getSto())
                    .build());

            return new BotRegistrationResult(true, registration.getNo(), approvalDuration);
        }
        else {
            user.setEnabled(true);
            String name = cache.getName().trim();
            String[] split = name.split(" ");

            if (split.length == 1) user.setFirstName(name);
            else {
                int i = name.lastIndexOf(" ");
                user.setFirstName(name.substring(0, i).trim());
                user.setLastName(name.substring(i).trim());
            }

            MultiValueMap<String, String> attributes = new LinkedMultiValueMap<>();
            attributes.set("phone", cache.getPhone());
            attributes.set("witel", cache.getWitel().name());

            if (StringUtils.isNotBlank(cache.getSto()))
                attributes.set("sto", cache.getSto());

            attributes.set("telegram", String.valueOf(cache.getId()));

            user.setAttributes(attributes);
            users.create(user);

            return new BotRegistrationResult(true, null, null);
        }
    }

    @Override
    public void registerFromApproval(String approvalNoOrId) {
        UserRegistration registration = userRegistrationRepo.findByIdOrNo(approvalNoOrId, approvalNoOrId)
                .orElseThrow(() -> new NotFoundException("registration not found"));


    }

}
