package dev.scaraz.mars.core.service.credential.impl;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.request.TelegramCreateUserDTO;
import dev.scaraz.mars.common.tools.enums.RegisterState;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.AppConstants;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nullable;
import java.util.List;

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
                                "Halo dan selamat datang di *Mars ROC-2*",
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
                .text(TelegramUtil.esc("Silahkan sebutkan NIK (Nomor Induk Karyawan) anda"))
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
                    .text(TelegramUtil.esc("NIK sudah terdaftar silahkan memasukkan ulang"))
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
        boolean existByPhone = userQueryService.existByCriteria(UserCriteria.builder()
                .phone(new StringFilter().setLike(ansPhone.trim()))
                .build());

        if (existByPhone) {
            return SendMessage.builder()
                    .chatId(registration.getId())
                    .text(TelegramUtil.esc("No hp sudah terdaftar silahkan memasukkan ulang"))
                    .build();
        }

        registration.setPhone(ansPhone);
        registration.setState(RegisterState.WITEL);
        registrationRepo.save(registration);
        return SendMessage.builder()
                .chatId(registration.getId())
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc(
                        "Silahkan sebutkan *Witel* anda",
                        "",
                        "_Anda bisa membalas pesan ini dengan menyebut nama *Witel* anda",
                        "atau dengan menekan tombol yang disediakan.",
                        "Jika anda menekan *Current* dibawah, *Witel* region anda akan disesuaikan dengan dimana bot (saya) di deploy_",
                        "",
                        "Deployment: *" + marsProperties.getWitel() + "*"
                ))
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(
                                InlineKeyboardButton.builder()
                                        .text(TelegramUtil.esc("Current"))
                                        .callbackData(AppConstants.Telegram.IGNORE_WITEL)
                                        .build()
                        ))
                        .build())
                .build();
    }

    @Override
    public SendMessage answerWitelThenAskSubregion(BotRegistration registration, Witel ansWitel) {
        if (ansWitel != Witel.ROC) {
            registration.setWitel(ansWitel);
            registration.setState(RegisterState.REGION);
            registrationRepo.save(registration);
            return SendMessage.builder()
                    .chatId(registration.getId())
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(TelegramUtil.esc("Silahkan sebutkan *STO* anda, atau tuliskan *WOC* jika anda merupakan helpdesk di kantor witel"))
                    .build();
        }

        return answerSubregionThenEnd(registration, null);
    }

    @Override
    @Transactional
    public SendMessage answerSubregionThenEnd(BotRegistration registration, @Nullable String ansSubRegion) {
        if (ansSubRegion != null) ansSubRegion = ansSubRegion.toUpperCase();

        userService.createFromBot(null, TelegramCreateUserDTO.builder()
                .telegramId(registration.getId())
                .phone(registration.getPhone())
                .nik(registration.getNik())
                .name(registration.getName())
                .witel(registration.getWitel())
                .sto(ansSubRegion)
                .build());

        registrationRepo.delete(registration);
        return SendMessage.builder()
                .chatId(registration.getId())
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.WELCOME_MESSAGE())
                .build();
    }

}
