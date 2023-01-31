package dev.scaraz.mars.common.utils;

import dev.scaraz.mars.common.tools.enums.Product;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;

public abstract class AppConstants {


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
        String USER_DASHBOARD_ROLE = "user_dashboard";
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
        long CLOSE_CONFIRM_ID_INT = 1;
        long ALLOW_OTHER_WITEL_ID_BOOL = 2;
        long USER_REG_APPROVAL_ID_BOOL = 3;
        long SEND_REG_APPROVAL_TO_ADMINS_ID_BOOL = 4;
        long POST_PENDING_CONFIRM_ID_INT = 5;
        long USER_REG_APPROVAL_DURATION_ID_INT = 6;
        long APPROVAL_ADMIN_EMAILS_ID_ARR = 7;

        static String title(long id) {
            switch ((int) id) {
                case (int) CLOSE_CONFIRM_ID_INT:
                    return "Lama Durasi Konfirmasi";
                case (int) ALLOW_OTHER_WITEL_ID_BOOL:
                    return "Witel lain diperbolehkan login";
                case (int) USER_REG_APPROVAL_ID_BOOL:
                    return "Registrasi Diperlukan Approval";
                case (int) SEND_REG_APPROVAL_TO_ADMINS_ID_BOOL:
                    return "null";
                case (int) POST_PENDING_CONFIRM_ID_INT:
                    return "Lama Waktu Status Pending";
                case (int) USER_REG_APPROVAL_DURATION_ID_INT:
                    return "Lama Waktu Approval";
                case (int) APPROVAL_ADMIN_EMAILS_ID_ARR:
                    return "Email admin untuk Approval";
            }

            return null;
        }
    }

}
