package dev.scaraz.mars.common.exception.web;

import org.zalando.problem.Status;

public class AccessDeniedException extends MarsException {

    public static final String TITLE = "Access Denied";

    public AccessDeniedException(String title, String message, Object... args) {
        super(Status.UNAUTHORIZED, title, message, args);
    }

    public AccessDeniedException(String message, Object... args) {
        this(TITLE, message, args);
    }

    public AccessDeniedException() {
        this(null);
    }

}
