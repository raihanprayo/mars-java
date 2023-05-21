package dev.scaraz.mars.common.domain.general;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditableDTO extends TimestampDTO {

    private String createdBy;

    private String updatedBy;

}
