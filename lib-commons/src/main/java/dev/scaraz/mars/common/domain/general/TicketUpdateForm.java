package dev.scaraz.mars.common.domain.general;

import dev.scaraz.mars.common.tools.enums.TcStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketUpdateForm {

    private String no;

    private TcStatus status;

    private String description;

}
