package dev.scaraz.mars.v1.core.service;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.request.ForgotReqDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.common.domain.response.ForgotResDTO;
import dev.scaraz.mars.v1.core.domain.credential.User;
import dev.scaraz.mars.security.authentication.MarsJwtAuthenticationToken;

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
