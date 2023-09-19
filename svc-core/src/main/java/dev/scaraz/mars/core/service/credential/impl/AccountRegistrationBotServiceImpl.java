package dev.scaraz.mars.core.service.credential.impl;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.request.TelegramCreateUserDTO;
import dev.scaraz.mars.common.exception.telegram.TgInvalidFormError;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.enums.RegisterState;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.tools.filter.type.WitelFilter;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.common.utils.ConfigConstants;
import dev.scaraz.mars.core.domain.cache.BotRegistration;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.order.Sto;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.repository.cache.BotRegistrationRepo;
import dev.scaraz.mars.core.repository.db.order.StoRepo;
import dev.scaraz.mars.core.service.ConfigService;
import dev.scaraz.mars.core.service.credential.AccountRegistrationBotService;
import dev.scaraz.mars.core.service.credential.AccountService;
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
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor

@Service
public class AccountRegistrationBotServiceImpl implements AccountRegistrationBotService {

    //    private final AppConfigService appConfigService;
    private final ConfigService configService;
    private final MarsProperties marsProperties;

    private final AccountService accountService;
    private final AccountQueryService accountQueryService;

    private final StoRepo stoRepo;
    private final BotRegistrationRepo registrationRepo;

    private final TelegramBotService botService;

    @Override
    public SendMessage pairAccount(long telegramId, String username) {
        try {
            botService.getClient().execute(SendMessage.builder()
                    .chatId(telegramId)
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(TelegramUtil.esc(
                            "Untuk keamanan saya akan bertanya beberapa hal,",
                            "dan jika diperlukan:",
                            "- ketik /reg\\_end untuk menghentikan proses integrasi",
                            "",
                            "_Jika selama 5 menit integrasi belum selesai, proses akan dihentikan_"
                    ))
                    .build());
        }
        catch (TelegramApiException e) {
            log.error("Unable to send header", e);
        }

        registrationRepo.save(BotRegistration.builder()
                .id(telegramId)
                .username(username)
                .state(RegisterState.PAIR_NIK)
                .build());

        return SendMessage.builder()
                .chatId(telegramId)
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc(
                        "Silahkan input NIK (Nomor Induk Karyawan) anda"
                ))
                .build();
    }

    @Override
    public SendMessage pairAccountAnsNik(BotRegistration registration, String ansNik) {
        registration.setNik(ansNik);
        registration.setState(RegisterState.PAIR_WITEL);
        registrationRepo.save(registration);
        return SendMessage.builder()
                .chatId(registration.getId())
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc("Silahkan sebutkan *Witel* anda"))
                .build();
    }

    @Override
    public SendMessage pairAccountAnsWitel(BotRegistration registration, Witel ansWitel) {
        Optional<Account> optUser = accountQueryService.findOne(UserCriteria.builder()
                .nik(new StringFilter().setEq(registration.getNik()))
                .witel(new WitelFilter().setEq(ansWitel))
                .build());

        if (optUser.isPresent()) {
            registration.setWitel(ansWitel);
            accountService.pairing(optUser.get(), registration);
            registrationRepo.deleteById(registration.getId());
            return SendMessage.builder()
                    .chatId(registration.getId())
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(TelegramUtil.esc(
                            "Berhasil melakukan penyesuaian",
                            "",
                            "_Selamat Datang di *MARS-ROC2*_"
                    ))
                    .build();
        }

        registrationRepo.deleteById(registration.getId());
        return SendMessage.builder()
                .chatId(registration.getId())
                .text(TelegramUtil.esc(
                        "NIK: " + registration.getNik(),
                        "Witel: " + ansWitel,
                        "user tidak ditemukan, mengakhiri proses pairing/integrasi"
                ))
                .build();
    }

    @Override
    public SendMessage start(long telegramId, String username) {
        if (!registrationRepo.existsById(telegramId)) {
            try {
                botService.getClient().execute(SendMessage.builder()
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

        registrationRepo.save(BotRegistration.builder()
                .id(telegramId)
                .username(username)
                .state(RegisterState.NAME)
                .build());

        return SendMessage.builder()
                .chatId(telegramId)
                .parseMode(ParseMode.MARKDOWNV2)
                .text("Silahkan sebutkan nama lengkap anda")
                .build();
    }

    @Override
    public SendMessage answerNameThenAskNik(BotRegistration registration, String ansName) {

        boolean existByName = accountQueryService.existByCriteria(UserCriteria.builder()
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
        boolean existByNik = accountQueryService.existByCriteria(UserCriteria.builder()
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
        boolean existByPhone = accountQueryService.existByCriteria(UserCriteria.builder()
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
                .text(TelegramUtil.esc("Silahkan sebutkan *Witel* anda"))
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(Witel.generateKeyboardButtons())
                        .build())
                .build();
    }

    @Override
    public SendMessage answerWitelThenAskSubregion(BotRegistration registration, Witel ansWitel) {
        registration.setWitel(ansWitel);

        if (ansWitel != Witel.ROC) {
            registration.setState(RegisterState.REGION);
            registrationRepo.save(registration);
            return SendMessage.builder()
                    .chatId(registration.getId())
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(TelegramUtil.esc("Silahkan sebutkan *STO* anda, atau tuliskan *WOC* jika anda merupakan help desk di kantor witel"))
                    .build();
        }

        return answerSubregionThenShowSummary(registration, null);
    }

    @Override
    @Transactional
    public SendMessage answerSubregionThenShowSummary(BotRegistration registration, @Nullable String ansSubRegion) {
        if (registration.getWitel() != Witel.ROC) {
            if (ansSubRegion == null)
                throw BadRequestException.args("Mohon input STO anda");

            if (!ansSubRegion.equalsIgnoreCase("WOC")) {
                Sto sto = stoRepo.findByWitelAndAliasIgnoreCase(registration.getWitel(), ansSubRegion)
                        .orElseThrow(() -> new TgInvalidFormError("STO", "STO tidak ditemukan"));
                ansSubRegion = sto.getAlias();
            }
        }

        registration.setSto(ansSubRegion);
        registrationRepo.save(registration);
        return SendMessage.builder()
                .chatId(registration.getId())
                .text(String.join("\n",
                        "Berikut ringkasan registrasi anda:",
                        "",
                        "Nama: " + registration.getName(),
                        "NIK: " + registration.getNik(),
                        "No. HP: " + registration.getPhone(),
                        "Witel: " + registration.getWitel(),
                        "STO: " + Objects.requireNonNullElse(registration.getSto(), "-"),
                        "",
                        "Apakah anda yakin dengan data tersebut?"
                ))
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(
                                InlineKeyboardButton.builder()
                                        .text("Ya")
                                        .callbackData(AppConstants.Telegram.REG_NEW_AGREE)
                                        .build(),
                                InlineKeyboardButton.builder()
                                        .text("Tidak")
                                        .callbackData(AppConstants.Telegram.REG_NEW_DISAGREE)
                                        .build()
                        ))
                        .build()
                ).build();
    }

    @Override
    public SendMessage answerSummary(BotRegistration registration, boolean agree) throws TelegramApiException {
        if (agree) {
            boolean needApproval = configService.get(ConfigConstants.APP_USER_REGISTRATION_APPROVAL_BOOL)
                    .getAsBoolean();

            accountService.createFromBot(needApproval, TelegramCreateUserDTO.builder()
                    .tgId(registration.getId())
                    .tgUsername(registration.getUsername())
                    .phone(registration.getPhone())
                    .nik(registration.getNik())
                    .name(registration.getName())
                    .witel(registration.getWitel())
                    .sto(registration.getSto() == null ?
                            null :
                            registration.getSto().toUpperCase())
                    .build());

            if (!needApproval) {
                return SendMessage.builder()
                        .chatId(registration.getId())
                        .parseMode(ParseMode.MARKDOWNV2)
                        .text(TelegramUtil.WELCOME_MESSAGE())
                        .build();
            }

            registrationRepo.deleteById(registration.getId());
        }

        botService.getClient().execute(SendMessage.builder()
                .chatId(registration.getId())
                .text("Mengulang pertanyaan registrasi")
                .build());
        return start(registration.getId(), registration.getUsername());
    }

}
