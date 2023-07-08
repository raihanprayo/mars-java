package dev.scaraz.mars.common.domain.response;

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
public class WhoamiDTO {
    private String id;
    private String nik;
    private String name;
    private Long telegramId;
    private Witel witel;
    private String sto;

    private String email;
    private String username;

    @Builder.Default
    private List<String> roles = new ArrayList<>();

}
