package dev.scaraz.mars.telegram.model.receiver;

import dev.scaraz.mars.telegram.util.Util;
import dev.scaraz.mars.telegram.util.enums.MessageSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
@Slf4j
public class TelegramReceiver {
    private final MessageSource source;
    private final String command;
    private final String argument;
    private final boolean isCommand;
    private final Long forwardedFrom;

    public TelegramReceiver(Update update) {
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
            String temp = text
                    .substring(commandEntity.getLength())
                    .trim();
            if (StringUtils.isBlank(temp)) argument = null;
            else argument = temp;
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

    private boolean isSlashStart(String message) {
        return message != null && message.trim()
                .startsWith("/");
    }

    private MessageEntity getCommandEntity(Message message) {
        MessageEntity result = null;
        List<MessageEntity> entities = isFromCaption() ?
                message.getCaptionEntities() :
                message.getEntities();

        if (entities != null) {
            for (MessageEntity entity : entities) {
                if (!entity.getType()
                        .equals("bot_command")) continue;
                result = entity;
            }
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof TelegramReceiver)) return false;

        TelegramReceiver that = (TelegramReceiver) o;

        return new EqualsBuilder()
                .append(command, that.command)
                .append(source, that.source)
                .append(isCommand(), that.isCommand())
                .append(getArgument(), that.getArgument())
                .append(getForwardedFrom(), that.getForwardedFrom())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(source)
                .append(isCommand())
                .append(getArgument())
                .append(isCommand())
                .append(getForwardedFrom())
                .toHashCode();
    }
}
