package com.sdney.rulesengine.domain.evaluations;

import com.sdney.rulesengine.domain.rule.ValueType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class ValueConverter {

    public Object convert(Object value, ValueType valueType) {

        if (value == null) {
            throw new IllegalArgumentException("Value is required");
        }
        if (valueType == null) {
            throw new IllegalArgumentException("Value type is required");
        }

        String textValue = value.toString();
        return switch (valueType) {
            case STRING ->
                    textValue;
            case INTEGER ->
                    Integer.valueOf(textValue);
            case DECIMAL ->
                    new BigDecimal(textValue);
            case BOOLEAN ->
                    convertBoolean(textValue);
            case DATE ->
                    LocalDate.parse(textValue);
        };
    }

    public List<Object> convertList(Object value, ValueType valueType) {

        if (value == null) {
            throw new IllegalArgumentException("Value is required");
        }
        if (valueType == null) {
            throw new IllegalArgumentException("Value type is required");
        }

        String textValue = value.toString();
        return Arrays.stream(textValue.split(","))
                .map(String::trim)
                .map(item -> convert(item, valueType))
                .toList();
    }

    private Boolean convertBoolean(String value) {

        if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
            throw new IllegalArgumentException("Invalid boolean value: " + value);
        }
        return Boolean.valueOf(value);
    }

}