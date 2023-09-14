package dev.scaraz.mars.app.telegram.web.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendTelegramResultDTO implements Serializable {

    private Integer messageId;

}
