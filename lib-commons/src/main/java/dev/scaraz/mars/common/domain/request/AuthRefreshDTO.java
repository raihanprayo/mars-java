package dev.scaraz.mars.common.domain.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AuthRefreshDTO {

    @NotNull
    private String refreshToken;

}
