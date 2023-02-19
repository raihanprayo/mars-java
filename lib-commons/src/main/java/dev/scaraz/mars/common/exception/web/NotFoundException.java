package dev.scaraz.mars.common.exception.web;

import org.zalando.problem.Status;

public class NotFoundException extends MarsException {

    public static final String TITLE = "Not Found";

    public NotFoundException(String title, String message, Object... args) {
        super(Status.NOT_FOUND, title, message, args);
    }

    public NotFoundException(String message, Object... args) {
        this(TITLE, message, args);
    }

    public NotFoundException() {
        this(TITLE, "entity.not.found");
    }

    public static NotFoundException args(String message, Object... args) {
        return new NotFoundException(message, args);
    }

    public static NotFoundException entity(Class<?> entity, String field, Object value) {
        return new NotFoundException("entity.not.found.detail", new Object[] {
                entity.getSimpleName(), field, value
        });
    }
}
