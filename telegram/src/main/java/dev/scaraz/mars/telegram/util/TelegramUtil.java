package dev.scaraz.mars.telegram.util;

import com.google.common.collect.ImmutableSet;
import dev.scaraz.mars.common.domain.general.TicketForm;
import dev.scaraz.mars.common.exception.telegram.TgError;
import dev.scaraz.mars.common.exception.web.MarsException;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.tools.annotation.FormDescriptor;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.telegram.model.TelegramBotCommand;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public abstract class TelegramUtil {
    public static final String TELEGRAM_EXECUTOR = "tg-bot-update-executor";
    public static final String RESERVED_CHAR_REGX = "([\\[\\])(~`>#+=|{}.!\\-])";

    public static final Comparator<Map.Entry<String, ?>> KEY_LENGTH_COMPARATOR =
            Comparator.comparing(Map.Entry::getKey, Comparator.comparingInt(String::length));

    public static final Comparator<TelegramBotCommand> TELEGRAM_BOT_COMMAND_COMPARATOR =
            Comparator.comparing(
                    TelegramBotCommand::getCommand,
                    Comparator.comparing(ImmutableSet.of("/license", "/help")::contains)
            ).thenComparing(TelegramBotCommand::getCommand);


    public static String esc(String... texts) {
        return Arrays.stream(texts)
                .map(t -> Arrays.stream(t.split("\n"))
                        .map(s -> s.replaceAll(RESERVED_CHAR_REGX, "\\\\$1"))
                        .collect(Collectors.joining("\n"))
                )
                .collect(Collectors.joining("\n"));
    }

    public static String exception(Exception ex) {
        if (isHandledError(ex)) {
            if (ex instanceof TgError)
                return esc(((TgError) ex).format());
            else if (ex instanceof MarsException) {
                MarsException me = (MarsException) ex;

                String title = me.getTitle();
                String causedTitle = "";

                Throwable caused = getExceptionRootCause(me);
                if (caused != null) {
                    causedTitle = "Caused by *" + caused.getClass().getSimpleName() + "*\n";
                }

                return esc("*" + title + "*:",
                        causedTitle,
                        me.getMessage()
                );
            }

            return "<unknown error>";
        }
        else {

            String title = ex.getClass().getSimpleName();
            String causedTitle = "";

            Throwable caused = getExceptionRootCause(ex);
            if (caused != null) {
                causedTitle = "Caused by *" + caused.getClass().getSimpleName() + "*\n";
            }

            return esc("*" + title + "*:",
                    causedTitle,
                    ex.getMessage(),
                    Translator.tr("tg.err.unhandled.footer")
            );
        }
    }

    public static String REPORT_FORMAT() {
        Map<String, FormDescriptor> desc = TicketForm.getDescriptors();

        String incidentAlias = String.join(",", desc.get("incident").alias());
        String issueAlias = String.join(",", desc.get("issue").alias());
        String productAlias = String.join(",", desc.get("product").alias());
        String serviceAlias = String.join(",", desc.get("service").alias());
        String descAlias = String.join(",", desc.get("description").alias());

        String witelStr = Stream.of(Witel.values())
                .map(Enum::name)
                .collect(Collectors.joining("/"));

        String productStr = Stream.of(Product.values())
                .map(Enum::name)
                .collect(Collectors.joining("/"));

        return Translator.tr("tg.ticket.report.format",
                incidentAlias,
                issueAlias,
                productAlias,
                serviceAlias,
                descAlias,
                witelStr,
                productStr
        );
    }

    public static String WELCOME_MESSAGE() {
        return esc(
                Translator.tr("app.welcome.text"),
                "\\n",
                REPORT_FORMAT()
        );
    }

    private static Throwable getExceptionRootCause(Throwable ex) {
        Throwable cause = ex.getCause();
        return cause == null ? null : getExceptionRootCause(cause);
    }

    private static boolean isHandledError(Throwable ex) {
        return ex instanceof MarsException || ex instanceof TgError;
    }

}
