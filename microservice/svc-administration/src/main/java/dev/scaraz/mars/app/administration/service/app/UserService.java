package dev.scaraz.mars.app.administration.service.app;

import dev.scaraz.mars.app.administration.domain.cache.UserRegistrationCache;
import dev.scaraz.mars.app.administration.web.dto.UserRegistrationDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.keycloak.representations.idm.UserRepresentation;

import java.time.Duration;
import java.util.Optional;

public interface UserService {
    UserRepresentation findByTelegramId(long telegramId) throws IllegalStateException;

    Optional<UserRepresentation> findByTelegramIdOpt(long telegramId);

    void registerNewUser(UserRegistrationDTO dto);

    BotRegistrationResult registerFromBot(UserRegistrationCache cache);

    void registerFromApproval(String approvalNoOrId);

    @Getter
    @AllArgsConstructor
    class BotRegistrationResult {
        private boolean onHold;
        private String registrationNo;
        private Duration expiredDuration;
    }
}
