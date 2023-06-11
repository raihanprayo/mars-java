package dev.scaraz.mars.user.web.dto;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String name;
    private String nik;
    private Long telegram;
    private String phone;
    private String email;

    private Witel witel;
    private String sto;

    private boolean enabled;

    @Builder.Default
    private List<String> roles = new ArrayList<>();
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
}
