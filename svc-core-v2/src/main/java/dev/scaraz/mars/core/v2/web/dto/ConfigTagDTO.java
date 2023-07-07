package dev.scaraz.mars.core.v2.web.dto;

import dev.scaraz.mars.core.v2.util.enums.DynamicType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigTagDTO {

    private int id;
    private String name;

}
