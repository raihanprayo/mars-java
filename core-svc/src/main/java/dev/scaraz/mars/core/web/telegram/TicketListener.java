package dev.scaraz.mars.core.web.telegram;

import dev.scaraz.mars.common.domain.general.TicketForm;
import dev.scaraz.mars.common.exception.telegram.TgInvalidFormError;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.service.order.TicketBotService;
import dev.scaraz.mars.core.util.Util;
import dev.scaraz.mars.core.util.annotation.TgAuth;
import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.annotation.Text;
import dev.scaraz.mars.telegram.util.ParseMode;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor

@TelegramBot
public class TicketListener {

    private final TicketBotService ticketBotService;

    @TelegramCommand(commands = "/report", description = "Register new ticker/order")
    public SendMessage registerReport(
            @TgAuth User user,
            @Text String text,
            Message message
    ) {
        try {
            TicketForm form = parseMessage(text);

            log.info("Validation Ticket Form {}", form);
            ticketBotService.validateForm(form);

            Ticket ticket = ticketBotService.registerForm(
                    form.toBuilder()
                            .source(getChatSource(message.getChat().getType()))
                            .senderId(user.getTelegramId())
                            .senderName(user.getName())
                            .build(),
                    message.getPhoto()
            );

            log.info("Sending Success notif");
            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .text(Translator.tr("ticket.registration.success", ticket.getNo()))
                    .build();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .text(TelegramUtil.esc(ex.getMessage()))
                    .parseMode(ParseMode.MarkdownV2.name())
                    .build();
        }
    }

    private TicketForm parseMessage(String text) {
        TicketForm form = new TicketForm();
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

    private static void applyForm(TicketForm form, String fieldValue, String fieldName) {
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
        Map<String, TicketForm.Descriptor> descriptors = TicketForm.getDescriptors();
        Set<String> formKeys = descriptors.keySet();
        for (String formKey : formKeys) {
            TicketForm.Descriptor descriptor = descriptors.get(formKey);
            if (descriptor.multiline()) continue;

            Stream<String> aliases = Stream.of(descriptor.alias());
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
        TicketForm.Descriptor descriptor = TicketForm.getDescriptors().get(FIELD_NAME);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            int colonIndex = line.indexOf(":");

            if (colonIndex == -1) continue;
            String fieldName = line.substring(0, colonIndex);

            Stream<String> aliases = Stream.of(descriptor.alias());
            if (aliases.anyMatch(alias -> alias.equalsIgnoreCase(fieldName))) {
                return i;
            }
            else if (fieldName.equalsIgnoreCase(FIELD_NAME)) {
                return i;
            }
        }
        return -1;
    }

    private TcSource getChatSource(String chatSource) {
        switch (chatSource.toUpperCase()) {
            case "PRIVATE":
                return TcSource.PRIVATE;
            case "GROUP":
                return TcSource.GROUP;
            default:
                return TcSource.OTHER;
        }
    }

}
