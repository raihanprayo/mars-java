package dev.scaraz.mars.common.domain.response;

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
public class WhoamiDTO {
    private String id;
    private String nik;
    private String name;
    private long telegramId;

    private String email;
    private String username;

    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Builder.Default
    private Group group = new Group();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Group {
        private String id;
        private String name;
    }

}
