package dev.scaraz.mars.common.tools.enums;

import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;

public enum QueryOperator {
    EQ("=="),

    NOT_EQ("!="),

    IN("[>]"),

    NOT_IN("![>]"),

    GT(">"),

    GTE(">="),

    LT("<"),

    LTE("<="),

    LIKE("%=");

    @Getter
    private final String operator;

    QueryOperator(String operator) {
        this.operator = operator;
    }

    public static QueryOperator byOperator(String operator) {
        for (QueryOperator value : QueryOperator.values()) {
            if (value.operator.equals(operator)) return value;
        }

        return QueryOperator.valueOf(operator.toUpperCase());
    }

}
