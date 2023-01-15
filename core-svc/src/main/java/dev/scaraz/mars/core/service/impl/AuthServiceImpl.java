package dev.scaraz.mars.core.service.impl;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.request.TelegramCreateUserDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.common.domain.response.JwtResult;
import dev.scaraz.mars.common.domain.response.JwtToken;
import dev.scaraz.mars.common.exception.telegram.TgUnauthorizedError;
import dev.scaraz.mars.common.exception.web.AccessDeniedException;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.exception.web.UnauthorizedException;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.config.datasource.AuditProvider;
import dev.scaraz.mars.core.config.security.CoreAuthenticationToken;
import dev.scaraz.mars.core.config.security.JwtUtil;
import dev.scaraz.mars.core.domain.credential.*;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.repository.credential.*;
import dev.scaraz.mars.core.service.credential.GroupService;
import dev.scaraz.mars.core.service.AuthService;
import dev.scaraz.mars.core.service.credential.UserService;
import dev.scaraz.mars.core.util.AuthSource;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static dev.scaraz.mars.common.utils.AppConstants.Auth.*;

@Slf4j
@RequiredArgsConstructor

@Service
public class AuthServiceImpl implements AuthService {

    private final AuditProvider auditProvider;
    private final GroupRepo groupRepo;

    private final RoleRepo roleRepo;
    private final RolesRepo rolesRepo;
    private final UserRepo userRepo;
    private final UserCredentialRepo userCredentialRepo;
    private final UserService userService;
    private final UserQueryService userQueryService;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        // bisa nik, telegramId, email, & username
        Optional<User> user = userRepo.findByNik(username);

        // Cari dengan telegram id
        if (user.isEmpty()) {
            try {
                long telegramId = Long.parseLong(username);
                user = userRepo.findByTelegramId(telegramId);
            }
            catch (NumberFormatException ex) {
            }
        }

        // Cari dengan username atau email
        if (user.isEmpty()) {
            user = userRepo.findByCredentialUsernameOrCredentialEmail(username, username);
        }

        // Jika masih ga ada
        if (user.isEmpty())
            throw new UsernameNotFoundException("No user found");

        return user.get();
    }

    @Override
    @Transactional
    public AuthResDTO authenticate(AuthReqDTO authReq, String application) {
        User user = loadUserByUsername(authReq.getNik());
        if (!user.hasRole(AppConstants.Authority.ADMIN_ROLE)) {
            if (!user.canLogin()) {
                throw new AccessDeniedException("auth.group.disable.login", user.getGroup().getName());
            }
        }

        boolean hasPassword = user.getCredential().getPassword() != null;
        if (!hasPassword) {
            if (!authReq.isConfirmed()) {
                return AuthResDTO.builder()
                        .code(PASSWORD_CONFIRMATION)
                        .build();
            }
            else {
                auditProvider.setName(user.getName());
                UserCredential credential = user.getCredential();
                credential.setEmail(authReq.getEmail());
                credential.setUsername(authReq.getUsername());
                credential.setPassword(passwordEncoder.encode(authReq.getPassword()));
                user.setCredential(userCredentialRepo.save(user.getCredential()));
            }
        }
        else {
            boolean passwordMatch = passwordEncoder.matches(authReq.getPassword(), user.getCredential().getPassword());
            if (!passwordMatch) {
                throw new UnauthorizedException("auth.user.invalid.password");
            }
        }

        Instant issuedAt = Instant.now();
        JwtResult accessToken = JwtUtil.accessToken(user, application, issuedAt);
        JwtResult refreshToken = JwtUtil.refreshToken(user, application, issuedAt);

        return AuthResDTO.builder()
                .code(SUCCESS)
                .user(user)
                .issuedAt(issuedAt.getEpochSecond())

                .accessToken(accessToken.getToken())
                .expiredAt(accessToken.getExpiredAt().getEpochSecond())
                .refreshToken(refreshToken.getToken())
                .refreshExpiredAt(refreshToken.getExpiredAt().getEpochSecond())
                .build();
    }

    @Override
    public User authenticateFromBot(long telegramId) {
        try {
            User user = userQueryService.findByTelegramId(telegramId);
            if (!user.isActive())
                throw new TgUnauthorizedError("Your account is not active, try to contact your administrator");

            SecurityContextHolder.getContext().setAuthentication(
                    new CoreAuthenticationToken(AuthSource.TELEGRAM, user)
            );
            LocaleContextHolder.setLocale(user.getSetting().getLang(), true);
            return user;
        }
        catch (NotFoundException ex) {
            throw new TgUnauthorizedError(telegramId);
        }
    }

    @Override
    public AuthResDTO refresh(String refreshToken) {
        try {
            JwtToken decode = JwtUtil.decode(refreshToken);
            String audience = decode.getAudience();
            User user = userRepo.findById(decode.getUserId())
                    .orElseThrow();

            Instant now = Instant.now();
            JwtResult jwtAccessToken = JwtUtil.accessToken(user, audience, now);
            JwtResult jwtRefreshToken = JwtUtil.refreshToken(user, audience, now);
            return AuthResDTO.builder()
                    .code(SUCCESS)
                    .accessToken(jwtAccessToken.getToken())
                    .expiredAt(jwtAccessToken.getExpiredAt().getEpochSecond())
                    .refreshToken(jwtRefreshToken.getToken())
                    .refreshExpiredAt(jwtRefreshToken.getExpiredAt().getEpochSecond())
                    .build();
        }
        catch (ExpiredJwtException ex) {
            return AuthResDTO.builder()
                    .code(RELOGIN_REQUIRED)
                    .build();
        }
    }

    @Override
    public boolean isUserRegistered(long telegramId) {
        try {
            userQueryService.findByTelegramId(telegramId);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    @Override
    @Transactional
    public SendMessage.SendMessageBuilder registerFromBot(
            MessageEntity entity,
            Message message) {

        // nama / nik / groupName / no. hp
        String text = message.getText()
                .substring(entity.getLength())
                .trim();

        org.telegram.telegrambots.meta.api.objects.User userTg = message.getFrom();

        String[] content = text.split("/");
        if (content.length != 4)
            throw new RuntimeException("Invalid content format");

        String name = content[0],
                nik = content[1],
                groupName = content[2],
                phone = content[3];

        Group group = groupRepo.findByNameIgnoreCase(groupName)
                .orElseThrow(() -> NotFoundException.entity(Group.class, "name", groupName));
        Role appRole = roleRepo.findByName("user")
                .orElseThrow(() -> NotFoundException.entity(Role.class, "name", "user"));

        User user = userService.createFromBot(group, TelegramCreateUserDTO.builder()
                .name(name)
                .nik(nik)
                .phone(phone)
                .telegramId(userTg.getId())
                .build());

        rolesRepo.saveAll(List.of(
                Roles.builder()
                        .user(user)
                        .role(appRole)
                        .build()
        ));

        return SendMessage.builder()
                .chatId(message.getChatId())
                .text("registration success")
                .allowSendingWithoutReply(true);
    }
}
