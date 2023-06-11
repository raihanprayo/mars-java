package dev.scaraz.mars.common.utils.csv;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.Witel;
import io.github.avew.CsvewConsumer;
import io.github.avew.CsvewParser;
import io.github.avew.CsvewValidationDTO;

import java.util.Collection;

public class MarsCsvParser extends CsvewParser {

    public void parseWitel(int line, int col, String columName, Object o, Collection<CsvewValidationDTO> validations, CsvewConsumer<Witel> consumeWitel) throws Exception {
        CsvewValidationDTO validation = parseNotNull(line, col, columName, o, validations);
        if (validation.isError()) return;

        try {
            String witelStr = o.toString().toUpperCase();
            Witel witel = Witel.valueOf(witelStr);

            consumeWitel.accept(witel);
        }
        catch (IllegalArgumentException ex) {
            validation = CsvewValidationDTO.builder()
                    .line(line)
                    .error(true)
                    .message("Unknown witel type")
                    .build();
            validations.add(validation);
        }
    }

    public void parseProduct(int line, int col, String columName, Object o, Collection<CsvewValidationDTO> validations, CsvewConsumer<Product> consumeWitel) throws Exception {
        CsvewValidationDTO validation = parseNotNull(line, col, columName, o, validations);
        if (validation.isError()) return;

        try {
            String witelStr = o.toString().toUpperCase();
            Product witel = Product.valueOf(witelStr);

            consumeWitel.accept(witel);
        }
        catch (IllegalArgumentException ex) {
            validation = CsvewValidationDTO.builder()
                    .line(line)
                    .error(true)
                    .message("Unknown product type")
                    .build();
            validations.add(validation);
        }
    }

}
