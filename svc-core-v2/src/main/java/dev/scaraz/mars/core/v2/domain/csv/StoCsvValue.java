package dev.scaraz.mars.core.v2.domain.csv;

import dev.scaraz.mars.common.tools.enums.Witel;
import io.github.avew.CsvewValue;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoCsvValue extends CsvewValue {
    private Witel witel;
    private String datel;
    private String code;
    private String name;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                code + ", " +
                name + ", " +
                witel + ", " +
                datel + "}";
    }
}
