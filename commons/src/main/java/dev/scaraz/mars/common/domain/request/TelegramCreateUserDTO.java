package dev.scaraz.mars.common.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelegramCreateUserDTO {
    private String name;
    private String nik;
    private String phone;
    private long telegramId;
    private String groupName;
}
