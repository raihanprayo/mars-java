package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.security.MarsUserContext;
import dev.scaraz.mars.security.authentication.identity.MarsAuthentication;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/telegram")
public class TelegramResource {

    private final TelegramBotService botService;
    private final AccountQueryService accountQueryService;

    @GetMapping("/send/contact/{nikOrTelegram}")
    public ResponseEntity<?> sendContact(@PathVariable String nikOrTelegram) throws TelegramApiException {
        MarsAuthentication accessToken = MarsUserContext.getAccessToken();
        if (accessToken.getTelegram() == null)
            throw new BadRequestException("Akunmu belum memiliki no telegram");

        Account account = accountQueryService.findByNikOrTelegramId(nikOrTelegram);

        if (Objects.equals(accessToken.getTelegram(), account.getTg().getId()))
            throw new BadRequestException("Mengirim kontak diri sendiri");

        botService.getClient().executeAsync(SendContact.builder()
                .chatId(accessToken.getTelegram())
                .allowSendingWithoutReply(true)
                .firstName(account.getName())
                .phoneNumber(account.getPhone())
//                .protectContent(true)
                .build());

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
