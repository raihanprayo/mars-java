package dev.scaraz.mars.telegram.util;

import org.telegram.telegrambots.meta.api.objects.MessageEntity;

public enum MessageEntityType {
    MENTION,
    HASHTAG,
    CASHTAG,
    BOT_COMMAND,
    URL,
    EMAIL,
    PHONE_NUMBER,
    BOLD,
    ITALIC,
    UNDERLINE,
    STRIKETHROUGH,
    SPOILER,
    CODE,
    PRE,
    TEXT_LINK;

    public static boolean isValid(String type) {
        try {
            resolve(type);
            return true;
        }
        catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public static boolean isValid(MessageEntity entity) {
        try {
            resolve(entity.getType());
            return true;
        }
        catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public static boolean is(MessageEntity entity, MessageEntityType predicate) {
        try {
            MessageEntityType resolve = resolve(entity.getType());
            return resolve == predicate;
        }
        catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public static MessageEntityType resolve(String type) {
        return valueOf(type.toUpperCase());
    }

}
