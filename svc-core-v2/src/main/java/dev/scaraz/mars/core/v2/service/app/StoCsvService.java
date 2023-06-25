package dev.scaraz.mars.core.v2.service.app;

import dev.scaraz.mars.common.utils.csv.MarsCsvParser;
import dev.scaraz.mars.core.v2.domain.csv.StoCsvValue;
import io.github.avew.CsvewResultReader;
import io.github.avew.reader.CsvewReader;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class StoCsvService extends CsvewReader<StoCsvValue> {

    private static final String[] CSV_HEADER = {
            "Witel",
            "Datel",
            "Code",
            "Name"
    };

    public CsvewResultReader<StoCsvValue> process(InputStream inputStream) {
        return process(0, inputStream);
    }

    @Override
    public CsvewResultReader<StoCsvValue> process(int i, InputStream inputStream) {
        MarsCsvParser parser = new MarsCsvParser();
        return read(i, inputStream, CSV_HEADER, ";", (line, values, validations, target) -> {
            parser.parseWitel(line, 0, CSV_HEADER[0], values[0], validations, target::setWitel);
            parser.parseNotNull(line, 1, CSV_HEADER[1], values[1], validations, target::setDatel);
            parser.parseNotNull(line, 2, CSV_HEADER[2], values[2], validations, target::setCode);
            parser.parseNotNull(line, 3, CSV_HEADER[3], values[3], validations, target::setName);
        });
    }

}
