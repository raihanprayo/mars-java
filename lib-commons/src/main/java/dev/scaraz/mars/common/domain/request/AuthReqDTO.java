package dev.scaraz.mars.common.domain.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AuthReqDTO {

    private String nik;

    @NotNull
    private String password;

    private boolean confirmed = false;
    private String email;

    @NotNull
    private String username;

}
