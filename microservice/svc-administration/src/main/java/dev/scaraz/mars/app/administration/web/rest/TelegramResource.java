package dev.scaraz.mars.app.administration.web.rest;

import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/tg")
public class TelegramResource {

    private final TelegramBotService botService;

    @PostMapping("/send")
    public ResponseEntity<?> send(
            @RequestParam(name = "callbackUrl", required = false) String callbackUrl,
            @RequestBody BotApiMethodMessage payload
    ) throws TelegramApiException {
        Message message = botService.getClient().execute(payload);
        if (StringUtils.isBlank(callbackUrl)) return ResponseEntity.ok(message);
        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/issues/{witel}")
    public void updateIssues(@PathVariable Witel witel) {

    }

}
