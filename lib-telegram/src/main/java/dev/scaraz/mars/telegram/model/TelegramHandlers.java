package dev.scaraz.mars.telegram.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Setter
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
        return Collections.unmodifiableMap(commandList);
    }

    public Map<String, TelegramHandler> getCallbackQueryList() {
        return Collections.unmodifiableMap(callbackQueryList);
    }

    public Map<String, TelegramHandler> getPatternCommandList() {
        return Collections.unmodifiableMap(patternCommandList);
    }

    public Map<Long, TelegramHandler> getForwardHandlerList() {
        return Collections.unmodifiableMap(forwardHandlerList);
    }

    public void addCommandHandler(String command, TelegramHandler handler) {
        this.commandList.put(command, handler);
    }

    public void addCallbackQueryHandler(String pattern, TelegramHandler handler) {
        this.callbackQueryList.put(pattern, handler);
    }

//    public TelegramHandler getDefaultMessageHandler() {
//        return defaultMessageHandler;
//    }

    public void setDefaultMessageHandler(TelegramHandler defaultMessageHandler) {
        if (this.defaultMessageHandler != null)
            log.warn("Override default message handler");

        this.defaultMessageHandler = defaultMessageHandler;
    }

//    public TelegramHandler getDefaultCallbackQueryHandler() {
//        if (this.defaultMessageHandler != null)
//            log.warn("Using default CallbackQuery Handler!");
//        return defaultCallbackQueryHandler;
//    }

    public void setDefaultCallbackQueryHandler(TelegramHandler defaultCallbackQueryHandler) {
        if (this.defaultCallbackQueryHandler != null)
            log.warn("Override default callback-query handler");
        this.defaultCallbackQueryHandler = defaultCallbackQueryHandler;
    }

//    public TelegramHandler getDefaultForwardHandler() {
//        return defaultForwardHandler;
//    }

//    public void setDefaultForwardHandler(TelegramHandler defaultForwardHandler) {
//        this.defaultForwardHandler = defaultForwardHandler;
//    }

//    public String getPrefixHelpMessage() {
//        return prefixHelpMessage;
//    }

//    public void setPrefixHelpMessage(String prefixHelpMessage) {
//        this.prefixHelpMessage = prefixHelpMessage;
//    }
}