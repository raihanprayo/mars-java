package dev.scaraz.mars.common.domain.general;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoDTO implements Serializable {
    public int id;
    public Witel witel;
    public String datel;
    public String alias;
    public String name;
}
