package dev.scaraz.mars.common.exception.web;

import dev.scaraz.mars.common.tools.Translator;
import org.zalando.problem.Status;

public class InternalServerException extends MarsException {

    public InternalServerException(String title, String message, Object... args) {
        super(Status.INTERNAL_SERVER_ERROR, title, message, args);
    }

    private InternalServerException(String message, Object... args) {
        super(Status.INTERNAL_SERVER_ERROR, "Internal Server Error", message, args);
    }

    public static InternalServerException args(String message, Object... args) {
        return new InternalServerException(message, args);
    }

    public static InternalServerException args(Throwable caused, String message, Object... args) {
        String text = Translator.tr(message, args) + " (" + caused.getMessage() + ")";
        return new InternalServerException(text);
    }

}
