package dev.scaraz.mars.csv.parser;

import dev.scaraz.mars.csv.parser.annotation.CsvColumn;
import dev.scaraz.mars.csv.parser.util.CsvHeaderList;
import dev.scaraz.mars.csv.parser.util.CsvSerializer;
import dev.scaraz.mars.csv.parser.validation.CsvValidation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class CsvReader<C extends CsvValue> {
    private static final String BOM_CHAR = "\uFEFF";

    protected final CsvHeaderList headers;

    public CsvReader() throws IllegalArgumentException {
        Class<C> type = getGenericType();
        boolean hasCtorWithNoParam = Arrays.stream(type.getDeclaredConstructors())
                .anyMatch(e -> e.getParameterCount() == 0);

        if (!hasCtorWithNoParam)
            throw new IllegalArgumentException("Unable to construct csv value (" + type.getCanonicalName() + "), require constructor with 0 param");

        this.headers = new CsvHeaderList();
        Field[] fields = type.getDeclaredFields();

        for (Field field : fields) {
            CsvColumn cc = field.getAnnotation(CsvColumn.class);

            if (cc == null) continue;

            String[] values = cc.value();
            if (values.length == 0) values = new String[]{field.getName()};

            headers.add(List.of(values));
        }
    }

    public List<String> getHeaders() {
        return headers.stream()
                .map(ls -> ls.get(0))
                .collect(Collectors.toList());
    }

    protected CsvResult<C> read(
            InputStream is,
            String delimeter,
            boolean validateHeader,
            CsvSerializer<C> serializer
    ) {
        Class<C> type = getGenericType();
        CsvResult<C> result = new CsvResult<>();
        List<CsvValidation> validations = result.getValidations();

        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        try {
            if (validateHeader) {
                String[] headerValue = br.readLine()
                        .replaceAll(BOM_CHAR, "")
                        .split(delimeter, -1);
                List<CsvValidation> headerValidations = validateHeader(List.of(headerValue));
                if (headerValidations.size() > 0) {
                    validations.addAll(headerValidations);
                    return result;
                }
            }

            AtomicInteger index = new AtomicInteger(0);
            String lineContent;
            while ((lineContent = br.readLine()) != null) {
                String[] cols = lineContent
                        .replaceAll(BOM_CHAR, "")
                        .split(delimeter, -1);
                int line = index.getAndIncrement();
                C value = type.getDeclaredConstructor().newInstance();

                value.setLine(line);
                value.setRawValue(cols);

                if (cols.length > headers.size()) {
                    validations.add(CsvValidation.error(
                            value.getLine(),
                            "the number of columns is not the same as the header"
                    ));
                    continue;
                }

                try {
                    serializer.read(value.getLine(), cols, validations, value);
                    result.getValues().add(value);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    CsvValidation.error(value.getLine(), e.getMessage())
                            .addTo(validations);
                }
            }
        }
        catch (IOException | NoSuchMethodException | InstantiationException | IllegalAccessException |
               InvocationTargetException e) {
            e.printStackTrace();
            CsvValidation.error(-1, "Fail to read csv content");
        }

        return result;
    }

    protected Class<C> getGenericType() {
        ParameterizedType param = (ParameterizedType) this.getClass().getGenericSuperclass();
        return (Class) param.getActualTypeArguments()[0];
    }

    private List<CsvValidation> validateHeader(List<String> valueHeaders) {
        int totalCol = headers.size();
        if (valueHeaders.size() != totalCol)
            return List.of(CsvValidation
                    .error(1, 0, "Csv header total columns is not the same as its class definition"));

        List<CsvValidation> validations = new ArrayList<>();
        for (int i = 0; i < totalCol; i++) {
            String header = valueHeaders.get(i)
                    .replaceAll("[^\\x20-\\x7e]", "")
                    .trim();

            if (!headers.match(i, header)) {
                String join = String.join("/", headers.get(i));
                validations.add(CsvValidation.error(
                        1, i, String.format(
                                "Csv header column %s should be '%s'",
                                i + 1,
                                join)));
            }
        }
        return validations;
    }

}
