package dev.scaraz.mars.common.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthReqDTO {

    private String nik;

    private String password;

    private boolean confirmed = false;
    private String email;
    private String username;

}
