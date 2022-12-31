package dev.scaraz.mars.telegram.model;

import dev.scaraz.mars.telegram.util.Util;

import dev.scaraz.mars.telegram.util.enums.MessageSource;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

/**
 * Telegram bot command splitted by command and arguments.
 *
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class TelegramMessageCommand {
    private final MessageSource source;
    private final String command;
    private final String argument;
    private final boolean isCommand;
    private final Long forwardedFrom;

    public TelegramMessageCommand(Update update) {
        Message message = update.getMessage();
        this.source = message.getPhoto() != null ?
                MessageSource.CAPTION :
                MessageSource.TEXT;

        String text = isFromCaption() ?
                message.getCaption() :
                message.getText();

        MessageEntity commandEntity = getCommandEntity(message);
        if (commandEntity != null && commandEntity.getOffset() == 0) {
            isCommand = true;
            command = commandEntity.getText();
            argument = text.substring(commandEntity.getLength()).trim();
        }
        else {
            command = null;
            argument = text;
            isCommand = false;
        }

        this.forwardedFrom = Optional.ofNullable(message.getForwardFrom())
                .map(User::getId)
                .orElse(null);
    }

    /**
     * Current command received from user.
     */
    public Optional<String> getCommand() {
        return Optional.ofNullable(command);
    }

    /**
     * Command arguments.
     */
    public Optional<String> getArgument() {
        return Optional.ofNullable(argument);
    }

    /**
     * {@code true} if current message is command.
     */
    public boolean isCommand() {
        return isCommand;
    }

    public boolean isFromText() {
        return source == MessageSource.TEXT;
    }

    public boolean isFromCaption() {
        return source == MessageSource.CAPTION;
    }

    /**
     * User ID, from whom this forward is originated.
     */
    public OptionalLong getForwardedFrom() {
        return Util.optionalOf(forwardedFrom);
    }

    @Override
    public String toString() {
        return "TelegramMessageCommand{" +
                "command='" + command + '\'' +
                ", argument='" + argument + '\'' +
                ", isCommand=" + isCommand +
                ", forwardedFrom=" + forwardedFrom +
                '}';
    }

    private boolean isSlashStart(String message) {
        return message != null && message.trim().startsWith("/");
    }

    private MessageEntity getCommandEntity(Message message) {
        MessageEntity result = null;
        List<MessageEntity> entities = isFromCaption() ?
                message.getCaptionEntities() :
                message.getEntities();

        for (MessageEntity entity : entities) {
            if (!entity.getType().equals("bot_command")) continue;
            result = entity;
        }

        return result;
    }

}
