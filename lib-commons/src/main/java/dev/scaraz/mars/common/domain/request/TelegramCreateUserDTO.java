package dev.scaraz.mars.common.domain.request;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelegramCreateUserDTO implements Serializable {

    private String name;

    private String nik;

    @Pattern(regexp = "[0-9]*", message = "invalid phone number format")
    private String phone;

    private long tgId;

    private String tgUsername;

    private Witel witel;

    private String sto;

}
