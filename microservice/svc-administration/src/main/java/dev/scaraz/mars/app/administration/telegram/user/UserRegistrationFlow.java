package dev.scaraz.mars.app.administration.telegram.user;

import dev.scaraz.mars.app.administration.domain.cache.FormRegistrationCache;
import dev.scaraz.mars.app.administration.repository.cache.FormRegistrationCacheRepo;
import dev.scaraz.mars.app.administration.service.app.UserApprovalService;
import dev.scaraz.mars.app.administration.service.app.UserService;
import dev.scaraz.mars.common.tools.enums.RegisterState;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserRegistrationFlow {

    private final static Map<RegisterState, SendMessage.SendMessageBuilder> PROMPT = new EnumMap<>(RegisterState.class);

    static {
        PROMPT.put(RegisterState.NAME, SendMessage.builder()
                .parseMode(ParseMode.MARKDOWNV2)
                .text("Silahkan sebutkan nama lengkap anda")
        );

        PROMPT.put(RegisterState.NIK, SendMessage.builder()
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc("Silahkan sebutkan NIK (Nomor Induk Karyawan) anda"))
        );

        PROMPT.put(RegisterState.PHONE, SendMessage.builder()
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc("Silahkan sebutkan no. hp anda"))
        );

        PROMPT.put(RegisterState.WITEL, SendMessage.builder()
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc("Silahkan sebutkan *Witel* anda"))
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(Witel.generateKeyboardButtons())
                        .build())
        );

        PROMPT.put(RegisterState.REGION, SendMessage.builder()
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc("Silahkan sebutkan *STO* anda, atau tuliskan *WOC* jika anda merupakan help desk di kantor witel"))
        );
    }

    private final UserService userService;
    private final UserApprovalService userApprovalService;
    private final FormRegistrationCacheRepo formRegistrationCacheRepo;
    private final TelegramBotService telegramBotService;

    public boolean isInRegistrationOrWaitList(long userId) {
        return isInRegistration(userId) || isInApprovalWaitList(userId);
    }

    public boolean isInRegistration(long userId) {
        return formRegistrationCacheRepo.existsById(userId);
    }

    public boolean isInApprovalWaitList(long userId) {
        return userApprovalService.isInApprovalWaitList(userId);
    }

    public FormRegistrationCache get(long userId) {
        return formRegistrationCacheRepo.findById(userId)
                .orElseThrow();
    }

    public FormRegistrationCache save(FormRegistrationCache cache) {
        return formRegistrationCacheRepo.save(cache);
    }

    public void deleteById(long userId) {
        formRegistrationCacheRepo.deleteById(userId);
    }

    public SendMessage getPrompt(FormRegistrationCache cache, RegisterState state) {
        SendMessage prompt = PROMPT.get(state)
                .chatId(cache.getId())
                .build();

        String text = prompt.getText();
        switch (state) {
            case NAME:
                if (StringUtils.isNotBlank(cache.getName()))
                    text += String.format(" \\(%s\\)", cache.getName());
                break;
            case NIK:
                if (StringUtils.isNotBlank(cache.getNik()))
                    text += String.format(" \\(%s\\)", cache.getNik());
                break;
            case PHONE:
                if (StringUtils.isNotBlank(cache.getPhone()))
                    text += String.format(" \\(%s\\)", cache.getPhone());
                break;
            case WITEL:
                if (cache.getWitel() != null)
                    text += String.format(" \\(%s\\)", cache.getWitel());
                break;
            case REGION:
                if (StringUtils.isNotBlank(cache.getSto()))
                    text += String.format(" \\(%s\\)", cache.getSto());
                break;
        }

        prompt.setText(text);
        return prompt;
    }

    public SendMessage start(long telegramId) {
        if (!formRegistrationCacheRepo.existsById(telegramId)) {
            try {
                telegramBotService.getClient().execute(SendMessage.builder()
                        .chatId(telegramId)
                        .parseMode(ParseMode.MARKDOWNV2)
                        .text(TelegramUtil.esc(
                                "Halo dan selamat datang di registrasi *Mars*",
                                "Jika diperlukan:",
                                "- ketik /reg\\_reset untuk mengulang proses registrasi",
                                "- ketik /reg\\_end untuk menghentikan proses registrasi",
                                "",
                                "_Jika selama 5 menit registrasi belum selesai, proses registrasi akan berhenti_"
                        ))
                        .build());
            }
            catch (TelegramApiException e) {
                log.error("Unable to send header", e);
            }
        }

        FormRegistrationCache cache = formRegistrationCacheRepo.findById(telegramId)
                .orElseGet(() -> FormRegistrationCache.builder()
                        .id(telegramId)
                        .state(RegisterState.NAME)
                        .ttl(Duration.ofMinutes(5).toSeconds())
                        .build()
                );

        cache.setState(RegisterState.NAME);
        return getPrompt(save(cache), RegisterState.NAME);
    }

    public void answer(FormRegistrationCache cache, String answer) {
        switch (cache.getState()) {
            case NAME:
                cache.setName(answer);
                break;
            case NIK:
                cache.setNik(answer);
                break;
            case PHONE:
                cache.setPhone(answer);
                break;
            case WITEL:
                cache.setWitel(Witel.fromCallbackData(answer));
                break;
            case REGION:
                cache.setSto(answer);
                break;
        }
        formRegistrationCacheRepo.save(cache);
    }

    public SendMessage summary(FormRegistrationCache cache) {
        return SendMessage.builder()
                .chatId(cache.getId())
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc(
                        "Berikut Ringkasan registrasi anda:",
                        "",
                        "Nama: " + cache.getName(),
                        "NIK: " + cache.getNik(),
                        "No. HP: " + cache.getPhone(),
                        "Witel: " + cache.getWitel(),
                        "STO: " + Objects.requireNonNullElse(cache.getSto(), "-"),
                        "",
                        "Apakah anda yakin dengan data tersebut?"
                ))
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(
                                InlineKeyboardButton.builder()
                                        .text("Tidak")
                                        .callbackData(AppConstants.Telegram.REG_NEW_DISAGREE)
                                        .build(),
                                InlineKeyboardButton.builder()
                                        .text("Ya")
                                        .callbackData(AppConstants.Telegram.REG_NEW_AGREE)
                                        .build()
                        ))
                        .build())
                .build();
    }

    public SendMessage end(long userId) {
        FormRegistrationCache cache = formRegistrationCacheRepo.findById(userId)
                .orElseThrow();

        // TODO: register new user
        UserService.RegistrationResult result = userService.createUserFromBot(cache);
        try {
            if (result.isOnHold()) {
                return SendMessage.builder()
                        .chatId(cache.getId())
                        .parseMode(ParseMode.MARKDOWNV2)
                        .text(TelegramUtil.esc(
                                "Registrasi *" + result.getRegistrationNo() + "*",
                                "Terima kasih, permintaan anda kami terima. Menunggu konfirmasi admin *MARS*",
                                "",
                                "_Jika dalam 1x" + result.getExpiredDuration().toHours() + " jam belum terkonfirmasi, silahkan mengirim kembali registrasimu_"
                        ))
                        .build();
            }
            else {
                return SendMessage.builder()
                        .chatId(cache.getId())
                        .parseMode(ParseMode.MARKDOWNV2)
                        .text(TelegramUtil.WELCOME_MESSAGE())
                        .build();
            }
        }
        finally {
            formRegistrationCacheRepo.deleteById(cache.getId());
        }
    }

    @EventListener(RedisKeyExpiredEvent.class)
    public void onRegistrationExpired(RedisKeyExpiredEvent<FormRegistrationCache> event) throws TelegramApiException {
        if (event.getValue() instanceof FormRegistrationCache) {
            FormRegistrationCache value = (FormRegistrationCache) event.getValue();

            telegramBotService.getClient().execute(SendMessage.builder()
                    .chatId(value.getId())
                    .text("Proses registrasi dihentikan")
                    .build());
        }
    }

}
