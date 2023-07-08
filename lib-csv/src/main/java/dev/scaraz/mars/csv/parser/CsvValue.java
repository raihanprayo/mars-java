package dev.scaraz.mars.csv.parser;

import lombok.Getter;
import lombok.Setter;

public interface CsvValue {
    int getLine();
    void setLine(int line);

    String[] getRawValue();
    void setRawValue(String[] rawValue);

    @Getter
    @Setter
    abstract class Impl implements CsvValue {
        private int line;
        private String[] rawValue;
    }
}
