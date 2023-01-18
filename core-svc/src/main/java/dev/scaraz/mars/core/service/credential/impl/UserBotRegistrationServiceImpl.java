package dev.scaraz.mars.core.service.credential.impl;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.request.TelegramCreateUserDTO;
import dev.scaraz.mars.common.tools.enums.RegisterState;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.core.domain.cache.BotRegistration;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.repository.cache.BotRegistrationRepo;
import dev.scaraz.mars.core.service.credential.UserBotService;
import dev.scaraz.mars.core.service.credential.UserService;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserBotRegistrationServiceImpl implements UserBotService {

    private final MarsProperties marsProperties;

    private final UserService userService;
    private final UserQueryService userQueryService;

    private final BotRegistrationRepo registrationRepo;

    private final TelegramBotService botService;

    @Override
    public SendMessage start(long telegramId) {
        if (!registrationRepo.existsById(telegramId)) {
            try {
                botService.getClient().execute(SendMessage.builder()
                        .chatId(telegramId)
                        .parseMode(ParseMode.MARKDOWNV2)
                        .text(TelegramUtil.esc(
                                "Jika diperlukan:",
                                "- ketik /reg\\_reset untuk mengulang selama proses registrasi sedang berlangsung",
                                "- ketik /end untuk menghentikan proses registrasi"
                        ))
                        .build());
            }
            catch (TelegramApiException e) {
                log.error("Unable to send header", e);
            }
        }

        registrationRepo.save(BotRegistration.builder()
                .id(telegramId)
                .state(RegisterState.NAME)
                .build());

        return SendMessage.builder()
                .chatId(telegramId)
                .parseMode(ParseMode.MARKDOWNV2)
                .text("Silahkan sebutkan nama anda")
                .build();
    }

    @Override
    public SendMessage answerNameThenAskNik(BotRegistration registration, String ansName) {

        boolean existByName = userQueryService.existByCriteria(UserCriteria.builder()
                .name(new StringFilter().setLike(ansName.trim()))
                .build());

        if (existByName) {
            return SendMessage.builder()
                    .chatId(registration.getId())
                    .text("Nama sudah digunakan silahkan memasukkan ulang nama anda")
                    .build();
        }

        registration.setName(ansName.trim());
        registration.setState(RegisterState.NIK);
        registrationRepo.save(registration);
        return SendMessage.builder()
                .chatId(registration.getId())
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc("Silahkan sebutkan NIK (Nomor Induk Kantor) anda"))
                .build();
    }

    @Override
    public SendMessage answerNikThenAskPhone(BotRegistration registration, String ansNik) {

        boolean existByNik = userQueryService.existByCriteria(UserCriteria.builder()
                .nik(new StringFilter().setLike(ansNik.trim()))
                .build());

        if (existByNik) {
            return SendMessage.builder()
                    .chatId(registration.getId())
                    .text(TelegramUtil.esc("No hp sudah digunakan silahkan memasukkan ulang no hp anda"))
                    .build();
        }

        registration.setNik(ansNik.trim());
        registration.setState(RegisterState.PHONE);
        registrationRepo.save(registration);
        return SendMessage.builder()
                .chatId(registration.getId())
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc("Silahkan sebutkan no. hp anda"))
                .build();
    }

    @Override
    public SendMessage answerPhoneThenAskWitel(BotRegistration registration, String ansPhone) {
        registration.setPhone(ansPhone);
        registration.setState(RegisterState.WITEL);
        registrationRepo.save(registration);
        return SendMessage.builder()
                .chatId(registration.getId())
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc(
                        "Silahkan sebutkan regional anda",
                        "",
                        "_Anda bisa membalas pesan ini dengan menyebut nama witel anda",
                        "atau Ignore/Skip pesan dengan menekan tombol yang disediakan,",
                        "jika anda menekan Ignore/Skip dibawah WITEL region anda disesuaikan dengan dimana bot (saya) di deploy_",
                        "",
                        "Deployment: *" + marsProperties.getWitel() + "*"
                ))
                .build();
    }

    @Override
    public SendMessage answerWitelThenAskSubregion(BotRegistration registration, Witel ansWitel) {
        registration.setWitel(ansWitel);
        registration.setState(RegisterState.REGION);
        registrationRepo.save(registration);
        return SendMessage.builder()
                .chatId(registration.getId())
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc("Silahkan sebutkan sub-regional anda"))
                .build();
    }

    @Override
    @Transactional
    public SendMessage answerSubregionThenEnd(BotRegistration registration, String ansSubRegion) {
        userService.createFromBot(null, TelegramCreateUserDTO.builder()
                .telegramId(registration.getId())
                .phone(registration.getPhone())
                .nik(registration.getNik())
                .name(registration.getName())
                .witel(registration.getWitel())
                .subregion(ansSubRegion)
                .build());

        registrationRepo.delete(registration);
        return SendMessage.builder()
                .chatId(registration.getId())
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.WELCOME_MESSAGE())
                .build();
    }

}
