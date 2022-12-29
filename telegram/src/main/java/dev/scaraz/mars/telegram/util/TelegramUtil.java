package dev.scaraz.mars.telegram.util;

import com.google.common.collect.ImmutableSet;
import dev.scaraz.mars.telegram.model.TelegramBotCommand;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public interface TelegramUtil {
    String TELEGRAM_EXECUTOR = "tg-bot-update-executor";
    String RESERVED_CHAR_REGX = "([\\[\\])(~`>#+=|{}.!\\-])";

    Comparator<Map.Entry<String, ?>> KEY_LENGTH_COMPARATOR =
            Comparator.comparing(Map.Entry::getKey, Comparator.comparingInt(String::length));

    Comparator<TelegramBotCommand> TELEGRAM_BOT_COMMAND_COMPARATOR =
            Comparator.comparing(
                    TelegramBotCommand::getCommand,
                    Comparator.comparing(ImmutableSet.of("/license", "/help")::contains)
            ).thenComparing(TelegramBotCommand::getCommand);


    static String esc(String... texts) {
        return Arrays.stream(texts)
                .map(t -> t.replaceAll(RESERVED_CHAR_REGX, "\\$1"))
                .collect(Collectors.joining("\n"));
    }

}
