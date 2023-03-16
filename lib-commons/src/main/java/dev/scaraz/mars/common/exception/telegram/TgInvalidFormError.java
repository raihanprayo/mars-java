package dev.scaraz.mars.common.exception.telegram;

import dev.scaraz.mars.common.tools.Translator;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class TgInvalidFormError extends TgError {
    private final String field;
    private final List<String> aliases;

    public TgInvalidFormError(
            String field,
            String message,
            Object... args
    ) {
        this(field, message, null, args);
    }

    public TgInvalidFormError(String field,
                              String message,
                              @Nullable List<String> aliases,
                              Object... args) {
        super("Invalid Form", Translator.tr(message, args));
        this.field = field;
        this.aliases = aliases != null ? aliases : new ArrayList<>();
    }

    @Override
    public String format() {
        String footer = "field: " + field + "";

        if (!aliases.isEmpty())
            footer += String.format("\nalias: %s", String.join("/ ", aliases));

        return format(getMessage(), "", footer);
    }

}
