package dev.scaraz.mars.common.exception.web;

import org.zalando.problem.Status;

public class BadRequestException extends MarsException {

    public static final String TITLE = "Bad Request";

    public BadRequestException(String title, String message, Object... args) {
        super(Status.BAD_REQUEST, title, message, args);
    }

    public BadRequestException(String message, Object... args) {
        this(TITLE, message, args);
    }

    public BadRequestException() {
        this(TITLE, (String) null);
    }

    public static BadRequestException args(String message, Object... args) {
        return new BadRequestException(message, args);
    }

    public static BadRequestException duplicateEntity() {
        return new BadRequestException("entity.duplicate.exist");
    }

    public static BadRequestException duplicateEntity(Class<?> entity, String field, Object value) {
        return new BadRequestException("entity.duplicate.exist.detail", new Object[]{entity.getName(), field, value});
    }

}
