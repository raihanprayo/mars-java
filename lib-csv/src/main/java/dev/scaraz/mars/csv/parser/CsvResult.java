package dev.scaraz.mars.csv.parser;

import dev.scaraz.mars.csv.parser.validation.CsvValidation;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CsvResult<C extends CsvValue> {

    @Builder.Default
    private List<C> values = new ArrayList<>();

    @Builder.Default
    private List<CsvValidation> validations = new ArrayList<>();

    public boolean hasError() {
        return validations.size() > 0;
    }

    public int getCount() {
        return values.size();
    }

}
