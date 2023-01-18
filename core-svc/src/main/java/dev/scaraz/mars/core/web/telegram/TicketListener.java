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
import dev.scaraz.mars.core.query.TicketAgentQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.repository.cache.StatusConfirmRepo;
import dev.scaraz.mars.core.service.NotifierService;
import dev.scaraz.mars.core.service.order.TicketBotService;
import dev.scaraz.mars.core.service.order.TicketService;
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
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor

@TelegramBot
public class TicketListener {

    private final TicketService service;
    private final TicketQueryService queryService;
    private final TicketAgentQueryService agentQueryService;
    private final TicketBotService botService;
    private final StatusConfirmRepo confirmRepo;

    private final NotifierService notifierService;

    @TelegramCommand(commands = {"/report", "/lapor"}, description = "Register new ticker/order")
    public SendMessage registerReport(
            @TgAuth User user,
            @Text String text,
            Message message
    ) {
        try {
            TicketBotForm form = parseTicketRegistration(text);

            log.info("Validation Ticket Form {}", form);
            botService.validateForm(form);

            Ticket ticket = botService.registerForm(
                    form.toBuilder()
                            .source(TcSource.fromType(message.getChat().getType()))
                            .senderId(user.getTelegramId())
                            .senderName(user.getName())
                            .build(),
                    message.getPhoto()
            );

            log.info("Reply with success message");
            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .text(Translator.tr("ticket.registration.success", ticket.getNo()))
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

    @TelegramCommand(commands = "/take", description = "take ticket by order no")
    public SendMessage takeTicket(@TgAuth User user, @Text String text) {
        try {
            log.info("TAKE ACTION BY {}", user.getTelegramId());
            Ticket ticket = botService.take(text);

            return SendMessage.builder()
                    .chatId(user.getTelegramId())
                    .text(TelegramUtil.esc(Translator.tr("tg.ticket.wip.agent", ticket.getNo())))
                    .parseMode(ParseMode.MARKDOWNV2)
                    .build();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
            return SendMessage.builder()
                    .chatId(user.getTelegramId())
                    .text(TelegramUtil.exception(ex))
                    .parseMode(ParseMode.MARKDOWNV2)
                    .build();
        }
    }


    @TelegramCommand(commands = "/close", description = "close ticket")
    public void closeTicket(@TgAuth User user, @Text String text, Message message) {
        // NOTE:
        // Parsing format: /close <ticket-no> [description]
        // required no tiket
        // optional description

        if (message.getReplyToMessage() != null) {
            long messageId = message
                    .getReplyToMessage()
                    .getMessageId();

            confirmRepo.findById(messageId).ifPresent(confirm -> {
                log.info("TICKET CLOSE CONFIRMATION -- MESSAGE ID={}", messageId);
                botService.confirmedClose(messageId, true, text);
            });
        }
    }


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

    private TicketBotForm parseTicketRegistration(String text) {
        TicketBotForm form = new TicketBotForm();
        String[] lines = text.split("\n");

        // parsing required field
        for (String line : lines) {
            line = line.trim();
            int colonIndex = line.indexOf(":");

            if (colonIndex == -1) continue;
            String fieldValue = line.substring(colonIndex + 1).trim();
            String fieldName = fieldNameMatcher(line.substring(0, colonIndex));

            if (fieldName == null) continue;
            applyForm(form, fieldValue, fieldName);
        }

        // parsing note/description field
        int noteFieldIndex = noteLineMatcher(lines);
        if (noteFieldIndex != -1) {
            String noteStart = lines[noteFieldIndex];

            List<String> noteValue = Stream.concat(
                    Stream.of(noteStart.substring(noteStart.indexOf(":") + 1)),
                    List.of(lines).subList(noteFieldIndex + 1, lines.length).stream()
            ).collect(Collectors.toList());

            form.setDescription(String.join(" ", noteValue));
        }

        return form;
    }

    private void applyForm(TicketBotForm form, String fieldValue, String fieldName) {
        try {
            switch (fieldName) {
                case "witel":
                    form.setWitel(Witel.valueOf(fieldValue.toUpperCase()));
                    break;
                case "sto":
                    form.setSto(fieldValue.toUpperCase());
                    break;
                case "incident":
                    form.setIncident(fieldValue);
                    break;
                case "issue":
                    form.setIssue(fieldValue);
                    break;
                case "service":
                    form.setService(fieldValue);
                    break;
                case "product":
                    form.setProduct(Product.valueOf(fieldValue.toUpperCase()));
                    break;
            }
        }
        catch (IllegalArgumentException ex) {
            Class<? extends Enum<?>> enumType = fieldName.equals("witel") ?
                    Witel.class : Product.class;

            throw new TgInvalidFormError(
                    fieldName,
                    "error.ticket.form.enum",
                    Util.enumToString("/", enumType)
            );
        }
    }


    private String fieldNameMatcher(String fieldName) {
        Map<String, FormDescriptor> descriptors = TicketBotForm.getDescriptors();
        Set<String> formKeys = descriptors.keySet();
        for (String formKey : formKeys) {
            FormDescriptor formDescriptor = descriptors.get(formKey);
            if (formDescriptor.multiline()) continue;

            Stream<String> aliases = Stream.of(formDescriptor.alias());
            if (aliases.anyMatch(alias -> alias.equalsIgnoreCase(fieldName))) {
                return formKey;
            }

            if (formKey.equalsIgnoreCase(fieldName))
                return formKey;
        }

        return null;
    }

    private int noteLineMatcher(String[] lines) {
        String FIELD_NAME = "description";
        FormDescriptor formDescriptor = TicketBotForm.getDescriptors().get(FIELD_NAME);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            int colonIndex = line.indexOf(":");

            if (colonIndex == -1) continue;
            String fieldName = line.substring(0, colonIndex);

            Stream<String> aliases = Stream.of(formDescriptor.alias());
            if (aliases.anyMatch(alias -> alias.equalsIgnoreCase(fieldName))) {
                return i;
            }
            else if (fieldName.equalsIgnoreCase(FIELD_NAME)) {
                return i;
            }
        }
        return -1;
    }

}
