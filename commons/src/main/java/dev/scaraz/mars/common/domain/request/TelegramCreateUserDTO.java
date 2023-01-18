package dev.scaraz.mars.common.domain.request;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelegramCreateUserDTO {

    private String name;

    private String nik;

    @Pattern(regexp = "[0-9]*", message = "invalid phone number format")
    private String phone;

    private long telegramId;

    private String groupName;

    private String subregion;

    private Witel witel;

}
