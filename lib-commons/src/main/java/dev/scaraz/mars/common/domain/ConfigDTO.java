package dev.scaraz.mars.common.domain;

import dev.scaraz.mars.common.domain.dynamic.DynamicType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigDTO {
    private String key;
    private String value;
    private String tag;
    private DynamicType type;
}
