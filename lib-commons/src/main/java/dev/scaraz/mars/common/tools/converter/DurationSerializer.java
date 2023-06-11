package dev.scaraz.mars.common.tools.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class DurationSerializer extends JsonSerializer<Duration> {

    @Override
    public void serialize(Duration duration, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String s = duration.toString();
        List<Double> result = writeAsArray(s);

        jsonGenerator.writeArray(
                result.stream()
                        .mapToDouble(d -> d)
                        .toArray(),
                0, result.size()
        );
    }

    private List<Double> writeAsArray(String duration) {
        List<Double> result = new ArrayList<>(List.of(
                0d,
                0d,
                0d,
                0d
        ));

        String text = duration.replaceAll("[PT]", "")
                .toLowerCase();
        int length = text.length();

        StringBuilder value = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);

            switch (c) {
                case 'd': {
                    String number = value.toString();
                    result.add(0, Double.parseDouble(number));
                    value = new StringBuilder();
                    break;
                }
                case 'h': {
                    String number = value.toString();
                    result.add(1, Double.parseDouble(number));
                    value = new StringBuilder();
                    break;
                }
                case 'm': {
                    String number = value.toString();
                    result.add(2, Double.parseDouble(number));
                    value = new StringBuilder();
                    break;
                }
                case 's': {
                    String number = value.toString();
                    result.add(3, Double.parseDouble(number));
                    value = new StringBuilder();
                    break;
                }
                default:
                    value.append(c);
            }
        }

        return result;
    }

}
