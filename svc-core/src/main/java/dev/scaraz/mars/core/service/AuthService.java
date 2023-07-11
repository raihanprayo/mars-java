package dev.scaraz.mars.core.service;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.request.ForgotReqDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.common.domain.response.ForgotResDTO;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.security.authentication.token.MarsWebAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {

    @Transactional
    AuthResDTO authenticate(
            HttpServletRequest request,
            AuthReqDTO authReq,
            String application
    );

    Account authenticateFromBot(long telegramId);

    AuthResDTO refresh(MarsWebAuthenticationToken authentication);

    @Transactional(readOnly = true)
    void logout(HttpServletRequest request, Account account, boolean confirmed);

    void logout(Account account, boolean confirmed);

    ForgotResDTO forgotPasswordFlow(ForgotReqDTO f);

    ForgotResDTO forgotRegenerateOtp(String token);

    boolean isUserRegistered(long telegramId);

    boolean isUserInApproval(long telegramId);
}
