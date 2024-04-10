package dev.scaraz.mars.core.web.telegram;

import dev.scaraz.mars.common.domain.general.TicketBotForm;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.tools.filter.type.TcStatusFilter;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.order.TicketConfirm;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import dev.scaraz.mars.core.repository.db.order.TicketConfirmRepo;
import dev.scaraz.mars.core.service.order.ConfirmService;
import dev.scaraz.mars.core.service.order.TicketBotInstantService;
import dev.scaraz.mars.core.service.order.TicketBotService;
import dev.scaraz.mars.core.service.order.TicketFormService;
import dev.scaraz.mars.core.util.annotation.TgAuth;
import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramCallbackQuery;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.annotation.context.CallbackData;
import dev.scaraz.mars.telegram.annotation.context.Text;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

import static dev.scaraz.mars.common.utils.AppConstants.Telegram.REPORT_ISSUE;

@Slf4j
@RequiredArgsConstructor

@TelegramBot
public class TicketListener {

    private final TicketBotService ticketBotService;
    private final TicketBotInstantService ticketBotInstantService;
    private final TicketConfirmRepo ticketConfirmRepo;

    private final TicketFormService ticketFormService;
    private final TicketQueryService ticketQueryService;

    private final ConfirmService confirmService;

    @TelegramCommand("/tiket")
    public SendMessage ticketInfo(@TgAuth Account account, @Text String text) {
        if (StringUtils.isBlank(text))
            throw new IllegalArgumentException("argument <no-tiket> tidak boleh kosong");

        return ticketBotService.info(text);
    }

    @TelegramCommand(commands = {"/report", "/lapor"}, description = "Register new ticker/order")
    public SendMessage registerReport(
            @TgAuth Account account,
            @Text String text,
            Message message
    ) {
        try {
            TicketBotForm form = ticketFormService.parseTicketRegistration(text);
            ticketFormService.parseTicketNote(form, text, null);

            log.info("Validation Ticket Form {}", form);
            ticketBotService.validateForm(form);

            Ticket ticket = ticketBotService.registerForm(
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
            Ticket ticket = ticketBotService.take(text);

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


            confirmService.findByIdOpt(messageId).ifPresent(confirm -> {
                log.info("TICKET REOPEN CONFIRMATION -- MESSAGE ID={}", messageId);
                ticketBotService.confirmedClose(messageId, false, text, message.getPhoto());
            });
        }
    }

    @TelegramCallbackQuery(REPORT_ISSUE + "*")
    public void onRegistrationIssuePicked(CallbackQuery callbackQuery,
                                          @CallbackData String data
    ) throws TelegramApiException {
        Integer messageId = callbackQuery.getMessage().getMessageId();
        if (!confirmService.existsById(messageId)) return;

        long issueId = Long.parseLong(data.substring(data.lastIndexOf(":") + 1));
        ticketBotInstantService.instantForm_answerIssue(messageId, issueId);
    }

    //    @TelegramCommand("/confirm")
    public void resume(@TgAuth Account account, @Text String text) {
        if (StringUtils.isNoneBlank(text)) {
            log.debug("Confirm Pending: {}", text);
            Optional<TicketConfirm> confirmOpt = ticketConfirmRepo.findByValueAndStatus(text, TicketConfirm.POST_PENDING);
            if (confirmOpt.isPresent()) {
                boolean ticketExist = ticketQueryService.exist(new TicketCriteria()
                        .setNo(new StringFilter().setEq(text.trim()))
                        .setSenderId(new LongFilter().setEq(account.getTg().getId()))
                        .setStatus(new TcStatusFilter().setEq(TcStatus.PENDING)));

                if (ticketExist) ticketBotService.endPendingEarly(confirmOpt.get().getId(), text);

            }
            else throw new BadRequestException("Gagal melakukan konfirmasi tiket (konfirmasi tidak ditemukan)");
        }
    }

    @TelegramCallbackQuery(AppConstants.Telegram.TICKET_FINISH_PENDING)
    public void resumePendingTicket(CallbackQuery cq) {
        int messageId = cq.getMessage().getMessageId();
        confirmService.findByIdOpt(messageId)
                .ifPresent(ticketConfirm -> ticketBotService.endPendingEarly(messageId, ticketConfirm.getValue()));
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
