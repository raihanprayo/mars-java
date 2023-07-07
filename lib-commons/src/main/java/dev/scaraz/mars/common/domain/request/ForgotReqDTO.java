package dev.scaraz.mars.common.domain.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
public class ForgotReqDTO {

    public enum State {
        GENERATE,
        VALIDATE,
        ACCOUNT_RESET
    }

    public enum Send {
        TELEGRAM,
        EMAIL
    }

    @Setter
    @NotNull
    private State state;

    // -- State: PICK_OPTION ------------------------------------
    private Send with;

    private String username;


    // -- State: VALIDATE_OTP -------------------------------------
    private String otp;

    private String token;


    // -- State: ACCOUNT_RESET -------------------------------------
    private String newPassword;

}
