package dev.scaraz.mars.user.web.dto;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StoDTO implements Serializable {
    @NotNull
    private String id;
    @NotNull
    private String name;
    @NotNull
    private String datel;
    @NotNull
    private Witel witel;
}
