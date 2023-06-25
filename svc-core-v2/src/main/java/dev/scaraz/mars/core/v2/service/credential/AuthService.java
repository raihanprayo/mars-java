package dev.scaraz.mars.core.v2.service.credential;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;

public interface AuthService {
    AuthResDTO token(AuthReqDTO authReq);
}
