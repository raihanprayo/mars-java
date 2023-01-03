package dev.scaraz.mars.common.exception.telegram;

import dev.scaraz.mars.common.tools.Translator;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class TgInvalidFormError extends TelegramError {
    private final String field;
    private final List<String> aliases;

    public TgInvalidFormError(
            String field,
            String message,
            Object... args
    ) {
        this(field, Translator.tr(message, args), null, args);
    }

    public TgInvalidFormError(String field,
                              String message,
                              @Nullable List<String> aliases,
                              Object... args) {
        super("Invalid Form", Translator.tr(message, args));
        this.field = field;
        this.aliases = Objects.requireNonNullElse(aliases, new ArrayList<>());
    }

    @Override
    public String getMessage() {
        return String.join("\n",
                getTitle(),
                "",
                String.format("%s, field (%s) alias (%s)", super.getMessage(), field,
                        String.join("/", aliases)
                )
        );
    }
}
