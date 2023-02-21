package dev.scaraz.mars.telegram.model;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TelegramHandlers {
    private final Map<String, TelegramHandler> commandList = new HashMap<>();
    private final Map<String, TelegramHandler> callbackQueryList = new HashMap<>();
    private final Map<String, TelegramHandler> patternCommandList = new HashMap<>();
    private final Map<Long, TelegramHandler> forwardHandlerList = new HashMap<>();
    private TelegramHandler defaultMessageHandler;
    private TelegramHandler defaultForwardHandler;
    private TelegramHandler defaultCallbackQueryHandler;
    private String prefixHelpMessage;

    public Map<String, TelegramHandler> getCommandList() {
        return commandList;
    }

    public Map<String, TelegramHandler> getCallbackQueryList() {
        return callbackQueryList;
    }

    public Map<String, TelegramHandler> getPatternCommandList() {
        return patternCommandList;
    }

    public Map<Long, TelegramHandler> getForwardHandlerList() {
        return forwardHandlerList;
    }

    public TelegramHandler getDefaultMessageHandler() {
        return defaultMessageHandler;
    }

    public void setDefaultMessageHandler(TelegramHandler defaultMessageHandler) {
        this.defaultMessageHandler = defaultMessageHandler;
    }

    public TelegramHandler getDefaultCallbackQueryHandler() {
        if (this.defaultMessageHandler != null) log.warn("Override existing default CallbackQuery Handler!");
        return defaultCallbackQueryHandler;
    }

    public void setDefaultCallbackQueryHandler(TelegramHandler defaultCallbackQueryHandler) {
        this.defaultCallbackQueryHandler = defaultCallbackQueryHandler;
    }

    public TelegramHandler getDefaultForwardHandler() {
        return defaultForwardHandler;
    }

    public void setDefaultForwardHandler(TelegramHandler defaultForwardHandler) {
        this.defaultForwardHandler = defaultForwardHandler;
    }

    public String getPrefixHelpMessage() {
        return prefixHelpMessage;
    }

    public void setPrefixHelpMessage(String prefixHelpMessage) {
        this.prefixHelpMessage = prefixHelpMessage;
    }
}