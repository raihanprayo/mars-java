package dev.scaraz.mars.common.domain.response;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TicketContactDTO implements Serializable {
    private String name;
    private String phone;
    private String link;
}
