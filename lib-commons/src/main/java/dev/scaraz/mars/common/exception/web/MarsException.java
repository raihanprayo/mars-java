package dev.scaraz.mars.common.exception.web;

import dev.scaraz.mars.common.tools.Translator;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class MarsException extends AbstractThrowableProblem {

    public MarsException(Status status, String title, String message, Object... args) {
        super(null,
                title != null ? Translator.tr(title) : null,
                status,
                message != null ? Translator.tr(message, args) : null
        );
    }

}