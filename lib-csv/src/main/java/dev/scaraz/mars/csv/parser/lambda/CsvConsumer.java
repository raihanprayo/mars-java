package dev.scaraz.mars.csv.parser.lambda;

@FunctionalInterface
public interface CsvConsumer<T> {

    void set(T value) throws Exception;

    default void check(T value) throws Exception {
        if (value != null) set(value);
    }

}
