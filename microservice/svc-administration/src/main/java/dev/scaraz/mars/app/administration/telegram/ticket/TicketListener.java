package dev.scaraz.mars.app.administration.telegram.ticket;

import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import lombok.RequiredArgsConstructor;

@TelegramBot
@RequiredArgsConstructor
public class TicketListener {

    @TelegramCommand({"/report", "/lapor"})
    public void register() {

    }

}
