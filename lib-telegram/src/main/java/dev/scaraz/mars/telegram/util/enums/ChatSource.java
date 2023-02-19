package dev.scaraz.mars.telegram.util.enums;

public enum ChatSource {
    PRIVATE,
    GROUP,
    SUPER_GROUP,
    CHANNEL;

    public static ChatSource fromType(String type) {
        if (type.equals("supergroup")) return SUPER_GROUP;
        return ChatSource.valueOf(type.toUpperCase());
    }
}
