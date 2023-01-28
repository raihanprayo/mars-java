package dev.scaraz.mars.core.service;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.core.domain.credential.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

public interface AuthService extends UserDetailsService {

    AuthResDTO authenticate(AuthReqDTO authReq, String application);

    User authenticateFromBot(long telegramId);

    AuthResDTO refresh(String refreshToken);

    boolean isUserRegistered(long telegramId);

    boolean isUserInApproval(long telegramId);
}