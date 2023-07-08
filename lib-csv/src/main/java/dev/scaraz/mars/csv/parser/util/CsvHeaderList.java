package dev.scaraz.mars.csv.parser.util;

import java.util.ArrayList;
import java.util.List;

public class CsvHeaderList extends ArrayList<List<String>> {
    public boolean match(int index, String predicate) {
        return get(index).stream().anyMatch(i -> i.equalsIgnoreCase(predicate));
    }
}
