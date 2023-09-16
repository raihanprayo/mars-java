package dev.scaraz.mars.core.web.telegram;

import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.core.domain.cache.BotRegistration;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.credential.AccountApproval;
import dev.scaraz.mars.core.repository.cache.BotRegistrationRepo;
import dev.scaraz.mars.core.service.AuthService;
import dev.scaraz.mars.core.service.credential.AccountApprovalService;
import dev.scaraz.mars.core.service.credential.AccountRegistrationBotService;
import dev.scaraz.mars.core.service.credential.AccountService;
import dev.scaraz.mars.core.util.annotation.TgAuth;
import dev.scaraz.mars.telegram.annotation.TelegramBot;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.annotation.context.CallbackData;
import dev.scaraz.mars.telegram.annotation.context.Text;
import dev.scaraz.mars.telegram.annotation.context.UserId;
import dev.scaraz.mars.telegram.config.TelegramHandlerMapper;
import dev.scaraz.mars.telegram.model.TelegramHandler;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.OptionalLong;

import static dev.scaraz.mars.common.tools.Translator.LANG_EN;
import static dev.scaraz.mars.common.tools.Translator.LANG_ID;
import static dev.scaraz.mars.core.service.NotifierService.UNREGISTERED_USER;
import static dev.scaraz.mars.core.service.NotifierService.WAITING_APPROVAL;

@Slf4j
@RequiredArgsConstructor

@TelegramBot
public class UserListener {

    private final AuthService authService;

    private final AccountService accountService;
    private final AccountApprovalService accountApprovalService;

    private final AccountRegistrationBotService accountRegistrationBotService;
    private final BotRegistrationRepo registrationRepo;

    @Lazy
    private final TelegramHandlerMapper telegramHandlerMapper;

    @TelegramCommand(commands = {"/register", "/reg"})
    public SendMessage register(@UserId long telegramId, Message message) {
        if (!authService.isUserRegistered(telegramId)) {
            log.info("Register New User");

            if (accountApprovalService.existsByTelegramId(telegramId)) {
                AccountApproval approval = accountApprovalService.findByTelegramId(telegramId);
                return WAITING_APPROVAL(telegramId, approval);
            }

            return SendMessage.builder()
                    .chatId(message.getChatId())
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(TelegramUtil.esc(
                            "User tidak dikenali oleh *MARS*. Harap melakukan registrasi terlebih dahulu, ",
                            "atau integrasi akun yang ada dengan akun telegram anda"
                    ))
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboardRow(UNREGISTERED_USER)
                            .build())
                    .build();
        }

        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(TelegramUtil.esc(Translator.tr("error.user.registered")))
                .build();
    }

    @TelegramCommand(commands = "/setting")
    public SendMessage setting(@TgAuth Account account,
                               @Text String text
    ) {
        if (checkSettingFormat(text)) {
            String[] split = text.split("[ =]");
            String cmd = split[0];
            String value = split[1];

            if (cmd.equalsIgnoreCase("lang")) {
                boolean isEn = value.equalsIgnoreCase("en");
                boolean isIdn = value.equalsIgnoreCase("id");
                if (isEn || isIdn) {
                    if (isEn) account.getSetting().setLang(LANG_EN);
                    else account.getSetting().setLang(LANG_ID);

                    accountService.save(account.getSetting());
                    return SendMessage.builder()
                            .chatId(account.getTg().getId())
                            .text("OK")
                            .build();
                }
                else {
                    return SendMessage.builder()
                            .chatId(account.getTg().getId())
                            .text("Bahasa yangbisa digunakan en/id (ignore-case)")
                            .build();
                }
            }
        }

        return null;
    }

    @TelegramCommand(commands = "/reg_reset")
    public SendMessage registrationReset(User user) {
        if (registrationRepo.existsById(user.getId()))
            return accountRegistrationBotService.start(user.getId(), user.getUserName());

        return null;
    }

    @TelegramCommand(commands = "/reg_end")
    public SendMessage registrationForceEnd(@UserId long telegramId) {
        if (registrationRepo.existsById(telegramId)) {
            registrationRepo.deleteById(telegramId);
            return SendMessage.builder()
                    .chatId(telegramId)
                    .text("Menghentikan proses registrasi")
                    .build();
        }
        return null;
    }

    public SendMessage registrationAnswerWitel(
            User user,
            @CallbackData String data
    ) {
        Optional<BotRegistration> registrationOpt = registrationRepo.findById(user.getId());
        if (registrationOpt.isPresent()) {
            Witel witel = Witel.fromCallbackData(data);
            return accountRegistrationBotService.answerWitelThenAskSubregion(registrationOpt.get(), witel);
        }

        return null;
    }

    private boolean checkSettingFormat(String text) {
        return text.split("[ =]").length == 2;
    }

    @PostConstruct
    private void registerWitelCallback() {
        telegramHandlerMapper.addHandlers(OptionalLong.empty(), t -> {
            for (Witel witel : Witel.values()) {
                log.debug("Add Witel Callback Query: {}", witel);
                try {
                    t.getCallbackQueryList().put(witel.callbackData(),
                            TelegramHandler.builder()
                                    .bean(this)
                                    .method(getClass().getDeclaredMethod("registrationAnswerWitel", User.class, String.class))
                                    .build()
                    );
                }
                catch (NoSuchMethodException e) {
                }
            }
        });
    }

}
