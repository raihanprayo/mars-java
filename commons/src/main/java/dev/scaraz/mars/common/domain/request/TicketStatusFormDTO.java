package dev.scaraz.mars.common.domain.request;

import dev.scaraz.mars.common.tools.enums.TcStatus;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatusFormDTO {

    private TcStatus status;

    private String note;

    private MultipartFile[] files;

}
