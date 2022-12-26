package dev.scaraz.mars.core.web.telegram;

import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor

@TelegramBot
public class TicketListener {

    @TelegramCommand(commands = "/report", description = "Register new ticker/order")
    public void registerReport() {

    }

}
