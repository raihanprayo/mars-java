package dev.scaraz.mars.csv.parser;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.csv.parser.lambda.CsvConsumer;
import dev.scaraz.mars.csv.parser.util.CsvParserUtil;
import dev.scaraz.mars.csv.parser.validation.CsvValidation;

import java.util.Collection;


public class CsvValueParser {

    public void notNull(int line, int col, String value, Collection<CsvValidation> validations, CsvConsumer<String> consumer) throws Exception {
        CsvValidation v = CsvParserUtil.notNull(line, col, value)
                .addTo(validations);
        if (!v.isError()) consumer.check(value);
    }

    public void witel(int line, int col, String value, boolean required, Collection<CsvValidation> validations, CsvConsumer<Witel> consumer) throws Exception {
        CsvValidation validation = CsvParserUtil.enumeration(line, col, required, value, Witel.class)
                .addTo(validations);
        if (!validation.isError()) consumer.check((Witel) validation.getValue());
    }

    public void product(int line, int col, String value, boolean required, Collection<CsvValidation> validations, CsvConsumer<Product> consumer) throws Exception {
        CsvValidation validation = CsvParserUtil.enumeration(line, col, required, value, Product.class)
                .addTo(validations);
        if (!validation.isError()) consumer.check((Product) validation.getValue());
    }

}
