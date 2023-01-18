package dev.scaraz.mars.common.utils;

public interface AppConstants {


    interface Auth {
        String SUCCESS = "ok";
        String PASSWORD_CONFIRMATION = "confirm-password";
        String RELOGIN_REQUIRED = "relogin";

        String COOKIE_TOKEN = "MARS-TOKEN";
        String COOKIE_REFRESH_TOKEN = "RF-MARS";
    }

    interface Telegram {
        String CONFIRM_AGREE = "ANS_AGREE";
        String CONFIRM_DISAGREE = "ANS_DISAGREE";
        String IGNORE_WITEL = "REG_IGNORE:WITEL";
    }


    interface Authority {
        String ADMIN_ROLE = "admin";
        String USER_ROLE = "user";

        String ADMIN_NIK = "000001413914";
    }

    interface Cache {
        String TC_CONFIRM_NS = "tc:confirm";
        String USR_REGISTRATION_NS = "usr:register";
    }

    interface Config {
        long CLOSE_CONFIRM_ID_INT = 1;
        long ALLOW_OTHER_WITEL_ID_BOOL = 2;
    }

}
