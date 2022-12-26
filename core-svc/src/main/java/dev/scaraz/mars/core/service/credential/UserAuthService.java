package dev.scaraz.mars.core.service.credential;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

public interface UserAuthService {

    AuthResDTO login(AuthReqDTO authReq, String application);

    AuthResDTO refresh(String refreshToken);

    boolean isUserRegistered(long telegramId);

    SendMessage.SendMessageBuilder registerFromBot(MessageEntity entity, Message message);
}
