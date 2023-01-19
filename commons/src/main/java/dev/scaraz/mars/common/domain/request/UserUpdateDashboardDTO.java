package dev.scaraz.mars.common.domain.request;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDashboardDTO {

    private String nik;
    private String phone;
    private Witel witel;
    private String sto;
    private Boolean active;

    @Builder.Default
    private UpdateRole roles = new UpdateRole();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRole {
        @Builder.Default
        private List<String> removed = new ArrayList<>();

        @Builder.Default
        private List<String> selected = new ArrayList<>();
    }
}
