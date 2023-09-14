package dev.scaraz.mars.app.administration.web.dto;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO implements Serializable {
    private Long telegramId;

    private String name;

    private String nik;

    private String phone;

    private Witel witel;

    private String sto;
}
