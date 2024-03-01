package dev.scaraz.mars.telegram.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TelegramHandlerResult {
    private final TelegramHandler handler;
    private final Object[] arguments;
}
