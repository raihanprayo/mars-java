package dev.scaraz.mars.common.utils;

import dev.scaraz.mars.common.tools.enums.Product;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

public abstract class AppConstants {

    public static final String RESET_ISSUE_INLINE_BTN_EVENT = "recreate-inline-btn";

    public static final ZoneId ZONE_LOCAL = ZoneId.of("Asia/Jakarta");

    public interface Auth {
        String SUCCESS = "ok";
        String PASSWORD_CONFIRMATION = "confirm-password";
        String RELOGIN_REQUIRED = "relogin";

        String COOKIE_TOKEN = "MARS-TOKEN";
        String COOKIE_REFRESH_TOKEN = "RF-MARS";
    }

    public interface Telegram {
        String CONFIRM_AGREE = "ANS_AGREE";
        String CONFIRM_DISAGREE = "ANS_DISAGREE";

        String REG_IGNORE_WITEL = "REG:IGNORE_WITEL";
        String REG_NEW = "REG:NEW";
        String REG_PAIR = "REG:PAIR";


        String REPORT_ISSUE = "REP:ISSUE:";

        MultiValueMap<Product, InlineKeyboardButton> ISSUES_BUTTON_LIST = new LinkedMultiValueMap<>();
    }


    public interface Authority {
        String ADMIN_ROLE = "admin";
        String AGENT_ROLE = "user_agent";
        String USER_ROLE = "user";

        String ADMIN_NIK = "000001413914";
    }

    public interface Cache {
        String TC_CONFIRM_NS = "tc:confirm";
        String TC_PENDING_QUEUE = "tc:pending";
        String USR_REGISTRATION_NS = "usr:register";
        String USR_APPROVAL_NS = "usr:approval";

        static String j(String... ns) {
            return String.join(":", ns);
        }
    }

    public interface Config {
        // DRT = Duration
        // INT = Integer
        // BOOL = Boolean
        // ARR = Array/List

        long CLOSE_CONFIRM_ID_DRT = 1;
        long ALLOW_OTHER_WITEL_ID_BOOL = 2;
        long USER_REG_APPROVAL_ID_BOOL = 3;
        long SEND_REG_APPROVAL_TO_ADMINS_ID_BOOL = 4;
        long POST_PENDING_CONFIRM_ID_DRT = 5;
        long USER_REG_APPROVAL_DURATION_ID_DRT = 6;
        long APPROVAL_ADMIN_EMAILS_ID_ARR = 7;
        long ALLOW_AGENT_CREATE_TICKET_BOOL = 8;
        long TELEGRAM_ISSUE_COLUMN_INT = 9;

        static String title(long id) {
            switch ((int) id) {
                case (int) CLOSE_CONFIRM_ID_DRT:
                    return "Lama Durasi Konfirmasi";
                case (int) ALLOW_OTHER_WITEL_ID_BOOL:
                    return "Witel lain diperbolehkan login";
                case (int) USER_REG_APPROVAL_ID_BOOL:
                    return "Registrasi Diperlukan Approval";
                case (int) SEND_REG_APPROVAL_TO_ADMINS_ID_BOOL:
                    return "null";
                case (int) POST_PENDING_CONFIRM_ID_DRT:
                    return "Lama Waktu Status Pending";
                case (int) USER_REG_APPROVAL_DURATION_ID_DRT:
                    return "Lama Waktu Approval";
                case (int) APPROVAL_ADMIN_EMAILS_ID_ARR:
                    return "Email admin untuk Approval";
                case (int) ALLOW_AGENT_CREATE_TICKET_BOOL:
                    return "Tiket dibuat Agen";
                case (int) TELEGRAM_ISSUE_COLUMN_INT:
                    return "Kolom Command Start";
            }

            return null;
        }
    }

    public interface MimeType {
        String JPEG = MediaType.IMAGE_JPEG_VALUE,
                PNG = MediaType.IMAGE_PNG_VALUE;

        String IMAGE_WEBP_VALUE = "image/webp";
        MediaType IMAGE_WEBP = MediaType.parseMediaType(IMAGE_WEBP_VALUE);

        String APPLICATION_CSV_VALUE = "application/csv";
        MediaType APPLICATION_CSV = MediaType.parseMediaType(APPLICATION_CSV_VALUE);

        String APPLICATION_XLS_VALUE = "application/vnd.ms-excel";
        MediaType APPLICATION_XLS = MediaType.parseMediaType(APPLICATION_XLS_VALUE);

        String APPLICATION_XLSX_VALUE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        MediaType APPLICATION_XLSX = MediaType.parseMediaType(APPLICATION_XLSX_VALUE);

        Map<String, MediaType> MAPPED_MIME_TYPE = Map.ofEntries(
                Map.entry("jpeg", MediaType.IMAGE_JPEG),
                Map.entry("jpg", MediaType.IMAGE_JPEG),
                Map.entry("png", MediaType.IMAGE_PNG),
                Map.entry("webp", IMAGE_WEBP),
                Map.entry("csv", APPLICATION_CSV),
                Map.entry("xls", APPLICATION_XLS),
                Map.entry("xlsx", APPLICATION_XLSX)
        );
    }

    public static final List<String> TICKET_CSV_HEADER = List.of(
            "Order No",
            "Witel",
            "STO",
            "Tiket NOSSA",
            "Service No",
            "Source",
            "Sender",
            "Gaul",
            "Kendala",
            "Produk",
            "Tgl Dibuat",
            "Tgl Diupdate"
    );
}
