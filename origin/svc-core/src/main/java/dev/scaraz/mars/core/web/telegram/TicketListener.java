package dev.scaraz.mars.core.web.telegram;

import dev.scaraz.mars.common.domain.general.TicketBotForm;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.tools.filter.type.TcStatusFilter;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.order.TicketConfirm;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import dev.scaraz.mars.core.repository.db.order.TicketConfirmRepo;
import dev.scaraz.mars.core.service.order.TicketBotService;
import dev.scaraz.mars.core.service.order.TicketFormService;
import dev.scaraz.mars.core.util.annotation.TgAuth;
import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.annotation.context.Text;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor

@TelegramBot
public class TicketListener {

    private final TicketBotService botService;
    private final TicketConfirmRepo confirmRepo;
    private final TicketFormService formService;
    private final TicketQueryService queryService;

    @TelegramCommand("/tiket")
    public SendMessage ticketInfo(@TgAuth Account account, @Text String text) {
        if (StringUtils.isBlank(text))
            throw new IllegalArgumentException("argument <no-tiket> tidak boleh kosong");

        return botService.info(text);
    }

    @TelegramCommand(commands = {"/report", "/lapor"}, description = "Register new ticker/order")
    public SendMessage registerReport(
            @TgAuth Account account,
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
                            .senderId(account.getTg().getId())
                            .senderName(account.getName())
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
            log.error("Reply with fail message, cause:", ex);

            String null_pointer = Optional.ofNullable(ex.getMessage())
                    .orElse("Null Pointer");
            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .text(TelegramUtil.esc(null_pointer))
                    .parseMode(ParseMode.MARKDOWNV2)
                    .build();
        }
    }

    @TelegramCommand(commands = {"/take", "/sayaambil"}, description = "take ticket by order no")
    public SendMessage takeTicket(@TgAuth Account account, @Text String text) {
        try {
            log.info("TAKE ACTION BY {}", account.getTg().getId());
            Ticket ticket = botService.take(text);

            return SendMessage.builder()
                    .chatId(account.getTg().getId())
                    .text(TelegramUtil.esc(Translator.tr("tg.ticket.wip.agent", ticket.getNo())))
                    .parseMode(ParseMode.MARKDOWNV2)
                    .build();
        }
        catch (Exception ex) {
            log.error("Error at command /take", ex);
            return SendMessage.builder()
                    .chatId(account.getTg().getId())
                    .text(TelegramUtil.exception(ex))
                    .parseMode(ParseMode.MARKDOWNV2)
                    .build();
        }
    }

    @TelegramCommand(commands = "/reopen", description = "close ticket")
    public void reopen(@TgAuth Account account, @Text String text, Message message) {
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
                botService.confirmedClose(messageId, false, text, message.getPhoto());
            });
        }
    }

    @TelegramCommand("/confirm")
    public void resume(@TgAuth Account account, @Text String text) {
        // Melanjutkan tiket dengan status pending

        if (StringUtils.isNoneBlank(text)) {
            Optional<TicketConfirm> confirmOpt = confirmRepo.findByValueAndStatus(text, TicketConfirm.POST_PENDING);
            if (confirmOpt.isPresent()) {
                boolean ticketExist = queryService.exist(TicketCriteria.builder()
                        .no(new StringFilter().setEq(text))
                        .senderId(new LongFilter().setEq(account.getTg().getId()))
                        .status(new TcStatusFilter().setEq(TcStatus.PENDING))
                        .build());

                if (ticketExist) botService.endPendingEarly(confirmOpt.get().getId(), text);
            }
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


}
