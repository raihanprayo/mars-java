package dev.scaraz.mars.core.web.telegram;

import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.utils.AppConstants;
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
import dev.scaraz.mars.telegram.annotation.TelegramCallbackQuery;
import dev.scaraz.mars.telegram.annotation.TelegramCommand;
import dev.scaraz.mars.telegram.annotation.context.CallbackData;
import dev.scaraz.mars.telegram.annotation.context.Text;
import dev.scaraz.mars.telegram.annotation.context.UserId;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

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

//    @Lazy
//    private final TelegramHandlerMapper telegramHandlerMapper;

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

    @TelegramCallbackQuery({AppConstants.Telegram.REG_NEW_AGREE, AppConstants.Telegram.REG_NEW_DISAGREE})
    public SendMessage registrationSummaryCallback(
            User user,
            @CallbackData String data
    ) throws TelegramApiException {
        Optional<BotRegistration> registration = registrationRepo.findById(user.getId());
        if (registration.isEmpty()) return null;

        return accountRegistrationBotService.answerSummary(
                registration.get(),
                AppConstants.Telegram.REG_NEW_AGREE.equals(data)
        );
    }

    private boolean checkSettingFormat(String text) {
        return text.split("[ =]").length == 2;
    }


}
