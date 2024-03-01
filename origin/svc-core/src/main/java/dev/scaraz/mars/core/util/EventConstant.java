package dev.scaraz.mars.core.util;

public final class EventConstant {
    private EventConstant() {
    }

    public static String
            WEB_LOGIN = "WEB_LOGIN",
            WEB_LOGOUT = "WEB_LOGOUT",
            UPDATE_TICKET = "UPDATE_TICKET";

    public static String
            FLOW_CLOSE_TICKET = "close-ticket",
            FLOW_PENDING_TICKET = "pending-ticket",
            FLOW_DISPATCH_TICKET = "dispatch-ticket";
}
