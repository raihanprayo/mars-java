package dev.scaraz.mars.core.service;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.request.ForgotReqDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.common.domain.response.ForgotResDTO;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.security.authentication.token.MarsJwtAuthenticationToken;

public interface AuthService {

    AuthResDTO authenticate(AuthReqDTO authReq, String application);

    User authenticateFromBot(long telegramId);

    AuthResDTO refresh(MarsJwtAuthenticationToken authentication);

    void logout(User user, boolean confirmed);

    ForgotResDTO forgotPasswordFlow(ForgotReqDTO f);

    ForgotResDTO forgotRegenerateOtp(String token);

    boolean isUserRegistered(long telegramId);

    boolean isUserInApproval(long telegramId);
}
