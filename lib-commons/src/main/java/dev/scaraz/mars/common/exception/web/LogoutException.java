package dev.scaraz.mars.common.exception.web;

import lombok.Getter;
import org.zalando.problem.Status;

public class LogoutException extends MarsException {

    @Getter
    private final String code;

    public LogoutException(Status status, String code, String title, String message, Object... args) {
        super(status, title, message, args);
        this.code = code;
    }

    public LogoutException(Status status, String code, String message) {
        this(status, code, "Logout", message);
    }

    public LogoutException(String code, String message) {
        this(Status.BAD_REQUEST, code, message);
    }

}
