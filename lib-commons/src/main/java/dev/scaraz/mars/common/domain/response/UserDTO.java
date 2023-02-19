package dev.scaraz.mars.common.domain.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.scaraz.mars.common.tools.converter.InstantDeserializer;
import dev.scaraz.mars.common.tools.converter.InstantSerializer;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String name;
    private String nik;
    private String phone;
    private String email;
    private Witel witel;
    private String sto;

    @Builder.Default
    private UserTgDTO tg = new UserTgDTO();

    private boolean active;

    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant createdAt;

    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant updatedAt;

    private GroupDTO group;

    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Builder.Default
    private UserSettingDTO setting = new UserSettingDTO();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserTgDTO {
        private Long id;
        private String username;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSettingDTO {
        private Locale lang;
    }

}
