package dev.scaraz.mars.core.web.telegram;

import dev.scaraz.mars.common.tools.annotation.FormDescriptor;
import dev.scaraz.mars.common.domain.general.TicketBotForm;
import dev.scaraz.mars.common.exception.telegram.TgInvalidFormError;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.repository.order.TicketConfirmRepo;
import dev.scaraz.mars.core.service.order.TicketBotService;
import dev.scaraz.mars.core.service.order.TicketFormService;
import dev.scaraz.mars.core.util.Util;
import dev.scaraz.mars.core.util.annotation.TgAuth;
import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.annotation.Text;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor

@TelegramBot
public class TicketListener {

    private final TicketBotService botService;
    private final TicketConfirmRepo confirmRepo;
    private final TicketFormService formService;

    @TelegramCommand(commands = {"/report", "/lapor"}, description = "Register new ticker/order")
    public SendMessage registerReport(
            @TgAuth User user,
            @Text String text,
            Message message
    ) {
        try {
            TicketBotForm form = formService.parseTicketRegistration(text);
            formService.parseTicketNote(form, text, null);

            log.info("Validation Ticket Form {}", form);
            botService.validateForm(form);

            Ticket ticket = botService.registerForm(
                    form.toBuilder()
                            .source(TcSource.fromType(message.getChat().getType()))
                            .senderId(user.getTg().getId())
                            .senderName(user.getName())
                            .build(),
                    message.getPhoto()
            );

            log.info("Reply with success message");
            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(TelegramUtil.esc(
                            String.format(
                                    "Request telah tercatat dan diterima dengan no order *%s*", ticket.getNo()),
                            "",
                            "Menunggu request diproses."
                    ))
                    .build();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());

            log.warn("Reply with fail message");
            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .text(TelegramUtil.esc(ex.getMessage()))
                    .parseMode(ParseMode.MARKDOWNV2)
                    .build();
        }
    }

    @TelegramCommand(commands = {"/take", "/sayaambil"}, description = "take ticket by order no")
    public SendMessage takeTicket(@TgAuth User user, @Text String text) {
        try {
            log.info("TAKE ACTION BY {}", user.getTg().getId());
            Ticket ticket = botService.take(text);

            return SendMessage.builder()
                    .chatId(user.getTg().getId())
                    .text(TelegramUtil.esc(Translator.tr("tg.ticket.wip.agent", ticket.getNo())))
                    .parseMode(ParseMode.MARKDOWNV2)
                    .build();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
            return SendMessage.builder()
                    .chatId(user.getTg().getId())
                    .text(TelegramUtil.exception(ex))
                    .parseMode(ParseMode.MARKDOWNV2)
                    .build();
        }
    }


//    @TelegramCommand(commands = "/close", description = "close ticket")
//    public void closeTicket(@TgAuth User user, @Text String text, Message message) {
//        // NOTE:
//        // Parsing format: /close <ticket-no> [description]
//        // required no tiket
//        // optional description
//
//        if (message.getReplyToMessage() != null) {
//            long messageId = message
//                    .getReplyToMessage()
//                    .getMessageId();
//
//            confirmRepo.findById(messageId).ifPresent(confirm -> {
//                log.info("TICKET CLOSE CONFIRMATION -- MESSAGE ID={}", messageId);
//                botService.confirmedClose(messageId, true, text);
//            });
//        }
//    }


    @TelegramCommand(commands = "/reopen", description = "close ticket")
    public void reopenTicket(@TgAuth User user, @Text String text, Message message) {
        // NOTE:
        // Parsing format: /close <ticket-no> [description]
        // required no tiket
        // optional description

        if (message.getReplyToMessage() != null) {
            long messageId = message
                    .getReplyToMessage()
                    .getMessageId();

            confirmRepo.findById(messageId).ifPresent(confirm -> {
                log.info("TICKET REOPEN CONFIRMATION -- MESSAGE ID={}", messageId);
                botService.confirmedClose(messageId, false, text);
            });
        }
    }

}
