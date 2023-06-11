package dev.scaraz.mars.user.domain.csv;

import dev.scaraz.mars.common.tools.enums.Witel;
import io.github.avew.CsvewValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;

@Getter
@Setter
@ToString
public class StoCsv extends CsvewValue {

    public String code;

    public Witel witel;

    public String datel;

    @Column
    public String name;

}
