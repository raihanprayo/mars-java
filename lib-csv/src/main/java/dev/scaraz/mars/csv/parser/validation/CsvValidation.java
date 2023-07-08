package dev.scaraz.mars.csv.parser.validation;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CsvValidation {
    private int line;
    private int col;
    private boolean error;
    private String message;
    private Object value;

    public CsvValidation addTo(Collection<CsvValidation> validations) {
        if (isError()) validations.add(this);
        return this;
    }

    public static CsvValidation error(int line, int col, String message) {
        return CsvValidation.builder()
                .error(true)
                .line(line)
                .col(col)
                .message(message)
                .build();
    }

    public static CsvValidation error(int line, String message) {
        return error(line, 0, message);
    }

    public static CsvValidation ok() {
        return new CsvValidation();
    }

    public static CsvValidation ok(Object o) {
        return CsvValidation.builder().value(o).build();
    }

}
