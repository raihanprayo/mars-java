package dev.scaraz.mars.common.domain.request;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDashboardDTO {

    private String nik;
    private String phone;
    private Witel witel;
    private String sto;
    private Boolean active;
    private String email;

    private UpdateTelegram tg;

    private List<String> roles;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTelegram {
        private String username;
    }

}
