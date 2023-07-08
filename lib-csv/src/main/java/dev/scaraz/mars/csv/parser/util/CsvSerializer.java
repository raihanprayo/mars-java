package dev.scaraz.mars.csv.parser.util;

import dev.scaraz.mars.csv.parser.validation.CsvValidation;
import dev.scaraz.mars.csv.parser.CsvValue;

import java.util.Collection;

@FunctionalInterface
public interface CsvSerializer<C extends CsvValue> {
    void read(int line, String[] columns, Collection<CsvValidation> validations, C value) throws Exception;
}
