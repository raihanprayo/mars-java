package dev.scaraz.mars.core.service.credential;

import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.core.domain.cache.BotRegistration;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface UserBotService {
    SendMessage start(long telegramId);

    SendMessage answerNameThenAskNik(BotRegistration registration, String ansName);

    SendMessage answerNikThenAskPhone(BotRegistration registration, String ansNik);

    SendMessage answerPhoneThenAskWitel(BotRegistration registration, String ansPhone);

    SendMessage answerWitelThenAskSubregion(BotRegistration registration, Witel ansWitel);

    SendMessage answerSubregionThenEnd(BotRegistration registration, String ansSubRegion);
}
