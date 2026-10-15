package com.sdney.rulesengine.domain.evaluations;

import com.sdney.rulesengine.domain.rule.ValueType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValueConverterTest {

    @Test
    void shouldConvert() {

        Object value = "1000";
        ValueType valueType = ValueType.INTEGER;

        ValueConverter converter = new ValueConverter();

        Object result = converter.convert(value, valueType);


        assertTrue(result.equals(1000));
        assertInstanceOf(Integer.class, result);

    }

    @Test
    void shouldRejectConvertValueNull() {
        Object value = null;
        ValueType valueType = ValueType.INTEGER;

        ValueConverter converter = new ValueConverter();

        assertThrows(IllegalArgumentException.class, () -> converter.convert(value, valueType));
    }

    @Test
    void shouldRejectConvertValueTypeNull() {
        Object value = 1000;
        ValueType valueType = null;

        ValueConverter converter = new ValueConverter();
        assertThrows(IllegalArgumentException.class, () -> converter.convert(value, valueType));
    }

    @Test
    void shouldConvertToList() {
        Object value = "1000, 2000, 3000, 4000, 5000";
        ValueType valueType = ValueType.INTEGER;
        ValueConverter converter = new ValueConverter();
        List<Object> result = converter.convertList(value, valueType);
         List<Object> expected = List.of(1000, 2000, 3000, 4000, 5000);


        assertEquals(expected, result);
        assertInstanceOf(Integer.class, result.get(0));

    }

    @Test
    void shouldRejectConvertToListWithNullValue() {
        Object value = null;
        ValueType valueType = ValueType.INTEGER;
        ValueConverter converter = new ValueConverter();
        assertThrows(IllegalArgumentException.class, ()-> converter.convertList(value, valueType));
    }

    @Test
    void shouldRejectConvertToListWithNullValueType() {
        Object value = "1000, 2000, 3000, 4000, 5000";
        ValueType valueType = null;
        ValueConverter converter = new ValueConverter();
        assertThrows(IllegalArgumentException.class, ()-> converter.convertList(value, valueType));
    }

    @Test
    void shouldConvertBoolean() {
        String value = "true";
        ValueType valueType = ValueType.BOOLEAN;
        ValueConverter converter = new ValueConverter();
        Object result = converter.convert(value, valueType);

        assertTrue(result.equals(true));
        assertInstanceOf(Boolean.class, result);
    }

    @Test
    void shouldRejectConvertBoolean() {
        String value = "beto";
        ValueType valueType = ValueType.BOOLEAN;
        ValueConverter converter = new ValueConverter();
        assertThrows(IllegalArgumentException.class, ()-> converter.convert(value, valueType));


    }




}