package dev.scaraz.mars.core.service.credential;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;

public interface UserAuthService {

    AuthResDTO login(AuthReqDTO authReq, String application);

    AuthResDTO refresh(String refreshToken);
}
