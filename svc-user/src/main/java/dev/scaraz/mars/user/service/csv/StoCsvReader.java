package dev.scaraz.mars.user.service.csv;

import dev.scaraz.mars.common.utils.csv.MarsCsvParser;
import dev.scaraz.mars.user.domain.csv.StoCsv;
import dev.scaraz.mars.user.mapper.StoMapper;
import io.github.avew.CsvewParser;
import io.github.avew.CsvewResultReader;
import io.github.avew.reader.CsvewReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class StoCsvReader extends CsvewReader<StoCsv> {

    private final StoMapper mapper;

    public CsvewResultReader<StoCsv> process(InputStream is) {
        return process(0, is);
    }

    @Override
    public CsvewResultReader<StoCsv> process(int i, InputStream is) {
        MarsCsvParser parser = new MarsCsvParser();
        return read(i, is, HEADER, ";", (line, cols, validations, value) -> {
            parser.parseWitel(line, 0, HEADER[0], cols[0], validations, value::setWitel);
            parser.parseNotNull(line, 1, HEADER[1], cols[1], validations, v -> value.setDatel(v.toUpperCase()));
            parser.parseNotNull(line, 2, HEADER[2], cols[2], validations, v -> value.setCode(v.toUpperCase()));
            parser.parseNotNull(line, 3, HEADER[3], cols[3], validations, v -> value.setName(v.toUpperCase()));
        });
    }

    private static final String[] HEADER = {
            "Witel",
            "Datel",
            "Kode",
            "Nama"
    };

}
