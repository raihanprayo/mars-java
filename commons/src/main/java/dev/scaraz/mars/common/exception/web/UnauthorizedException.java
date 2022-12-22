package dev.scaraz.mars.common.exception.web;

import org.zalando.problem.Status;

public class UnauthorizedException extends MarsException {

    public static final String TITLE = "Unauthorized";

    public UnauthorizedException(String title, String message, Object... args) {
        super(Status.UNAUTHORIZED, title, message, args);
    }

    public UnauthorizedException(String message, Object... args) {
        this(TITLE, message, args);
    }

    public UnauthorizedException() {
        this(null);
    }

}
