package dev.scaraz.mars.common.tools.enums;

public enum TcSource {
    PRIVATE,
    GROUP,
    OTHER;

    public static TcSource fromType(String type) {
        switch (type.toUpperCase()) {
            case "PRIVATE":
                return TcSource.PRIVATE;
            case "GROUP":
                return TcSource.GROUP;
            default:
                return TcSource.OTHER;
        }
    }
}
