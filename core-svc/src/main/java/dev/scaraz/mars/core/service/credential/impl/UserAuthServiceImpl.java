package dev.scaraz.mars.core.service.credential.impl;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.request.TelegramCreateUserDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.common.domain.response.JwtResult;
import dev.scaraz.mars.common.domain.response.JwtToken;
import dev.scaraz.mars.common.exception.web.AccessDeniedException;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.exception.web.UnauthorizedException;
import dev.scaraz.mars.core.config.datasource.AuditProvider;
import dev.scaraz.mars.core.config.security.JwtUtil;
import dev.scaraz.mars.core.domain.credential.*;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.repository.credential.*;
import dev.scaraz.mars.core.service.credential.GroupService;
import dev.scaraz.mars.core.service.credential.UserAuthService;
import dev.scaraz.mars.core.service.credential.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import static dev.scaraz.mars.common.utils.AppConstants.Auth.*;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserAuthServiceImpl implements UserAuthService {

    private final AuditProvider auditProvider;
    private final GroupRepo groupRepo;
    private final GroupService groupService;

    private final RoleRepo roleRepo;
    private final RolesRepo rolesRepo;
    private final UserRepo userRepo;
    private final UserCredentialRepo userCredentialRepo;
    private final UserService userService;
    private final UserQueryService userQueryService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResDTO login(AuthReqDTO authReq, String application) {
        User user = userQueryService.loadUserByUsername(authReq.getNik()).getUser();

        if (!user.hasRole("admin")) {
            if (!user.canLogin()) {
                throw new AccessDeniedException("auth.group.disable.login");
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
                user.getCredential().setPassword(passwordEncoder.encode(authReq.getPassword()));
                user.setCredential(userCredentialRepo.save(user.getCredential()));
            }
        }
        else {
            boolean passwordMatch = passwordEncoder.matches(authReq.getPassword(), user.getCredential().getPassword());
            if (!passwordMatch) {
                throw new UnauthorizedException("auth.user.invalid.password");
            }
        }

        JwtResult accessToken = JwtUtil.accessToken(user, application);
        JwtResult refreshToken = JwtUtil.refreshToken(user, application);

        user.setCredential(userCredentialRepo.save(user.getCredential()));
        return AuthResDTO.builder()
                .code(SUCCESS)
                .user(user)
                .accessToken(accessToken.getToken())
                .expiredAt(accessToken.getExpiredAt().getEpochSecond())
                .refreshToken(refreshToken.getToken())
                .refreshExpiredAt(refreshToken.getExpiredAt().getEpochSecond())
                .build();
    }

    @Override
    public AuthResDTO refresh(String refreshToken) {
        try {
            JwtToken decode = JwtUtil.decode(refreshToken);
            String audience = decode.getAudience();
            User user = userRepo.findById(decode.getUserId())
                    .orElseThrow();

            JwtResult jwtAccessToken = JwtUtil.accessToken(user, audience);
            JwtResult jwtRefreshToken = JwtUtil.refreshToken(user, audience);
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
    public SendMessage.SendMessageBuilder registerFromBot(MessageEntity entity, Message message) {
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

        Group group = groupRepo.findByName(groupName)
                .orElseThrow(() -> NotFoundException.entity(Group.class, "name", groupName));
        Role role = roleRepo.findByNameAndGroupIsNull("user")
                .orElseThrow(() -> NotFoundException.entity(Role.class, "name", "user"));

        User user = userService.createFromBot(group, TelegramCreateUserDTO.builder()
                .name(name)
                .nik(nik)
                .phone(phone)
                .telegramId(userTg.getId())
                .build());

        rolesRepo.save(Roles.builder()
                .user(user)
                .role(role)
                .build());

        groupService.addUser(group, user, null);
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text("registration success")
                .allowSendingWithoutReply(true);
    }

}
