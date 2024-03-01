package dev.scaraz.mars.common.utils;

import java.util.Map;

public class RealmConstant {
    private RealmConstant() {
    }

    public static final Map<String, String> PERMISSIONS;
    public static final String
            ROLE_COMPOSITE_ADMINISTRATOR = "admin";

    public static final String
            CLIENT_MARS_ADMIN_RESOURCE = "mars-admin-resource",
            CLIENT_MARS_WITEL_RESOURCE = "mars-witel-resource";

    public interface Permission {
        Map.Entry<String, String> TICKET_CREATE = Map.entry(
                "create-ticket",
                "user diperbolehkan membuat tiket"
        );
        Map.Entry<String, String> TICKET_PROCESS = Map.entry(
                "process-ticket",
                "user diperbolehkan memproses tiket"
        );
        Map.Entry<String, String> TICKET_QUERY = Map.entry(
                "query-ticket",
                "user diperbolehkan melihat tiket"
        );

        Map.Entry<String, String> STO_MANAGE = Map.entry(
                "manage-sto",
                "user diperbolehkan membuat/mengedit STO"
        );
        Map.Entry<String, String> STO_QUERY = Map.entry(
                "query-sto",
                "user diperbolehkan melihat sto"
        );

        Map.Entry<String, String> ISSUE_MANAGE = Map.entry(
                "manage-issue",
                "user diperbolehkan membuat/mengubah issue"
        );
        Map.Entry<String, String> ISSUE_QUERY = Map.entry(
                "query-issue",
                "user diperbolehkan melihat issue"
        );
    }

    static {
        PERMISSIONS = Map.ofEntries(
                Permission.TICKET_CREATE,
                Permission.TICKET_PROCESS,
                Permission.TICKET_QUERY,
                Permission.STO_MANAGE,
                Permission.STO_QUERY,
                Permission.ISSUE_MANAGE,
                Permission.ISSUE_QUERY
        );
    }

}
