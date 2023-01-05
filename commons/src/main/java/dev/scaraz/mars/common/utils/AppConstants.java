package dev.scaraz.mars.common.utils;

public interface AppConstants {


    interface Auth {
        String SUCCESS = "ok";
        String PASSWORD_CONFIRMATION = "confirm-password";
        String RELOGIN_REQUIRED = "relogin";

        String COOKIE_TOKEN = "MARS-TOKEN";
        String COOKIE_REFRESH_TOKEN = "RF-MARS";
    }

    interface Ticket {
        String CONFIRM_AGREE = "TC-CON-AGREE";
        String CONFIRM_DISAGREE = "TC-CON-DISAGREE";
    }

}
