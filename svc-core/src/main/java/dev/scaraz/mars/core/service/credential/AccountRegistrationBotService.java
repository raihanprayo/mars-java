package dev.scaraz.mars.core.service.credential;

import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.core.domain.cache.BotRegistration;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface AccountRegistrationBotService {

    SendMessage pairAccount(long telegramId, String username);

    SendMessage pairAccountAnsNik(BotRegistration registration, String ansNik);

    SendMessage pairAccountAnsWitel(BotRegistration registration, Witel ansWitel);

    SendMessage start(long telegramId, String username);

    SendMessage answerNameThenAskNik(BotRegistration registration, String ansName);

    SendMessage answerNikThenAskPhone(BotRegistration registration, String ansNik);

    SendMessage answerPhoneThenAskWitel(BotRegistration registration, String ansPhone);

    SendMessage answerWitelThenAskSubregion(BotRegistration registration, Witel ansWitel);

    SendMessage answerSubregionThenShowSummary(BotRegistration registration, String ansSubRegion);

    SendMessage answerSummary(BotRegistration registration, boolean agree) throws TelegramApiException;
}
