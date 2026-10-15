package com.sdney.rulesengine.domain.evaluations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FactsTest {

    @Test
    void shouldCreateFacts() {

      Map<String, Object> values = Map.of(
              "amount", 15000,
              "country", "AO"
      );

      Facts facts = new Facts(values);

      assertAll(
              ()-> assertEquals(15000, facts.get("amount")),
              ()-> assertEquals("AO", facts.get("country"))
      );


    }

    @Test
    void shouldRejectCreateFactsWithNullValues() {

      Map<String, Object> values = null;

      assertThrows(IllegalArgumentException.class, () -> new Facts(values));

    }

    @Test
    void shouldRejectCreateFactsWithEmptyValues() {
        Map<String, Object> values = Map.of();
        assertThrows(IllegalArgumentException.class, () -> new Facts(values));
    }

    @Test
    void shouldRejectCreateFactsWithNullKey() {
        Map<String, Object> values = new HashMap<>();
        values.put(null,"country");

        assertThrows(IllegalArgumentException.class, () -> new Facts(values));
    }

    @Test
    void shouldRejectCreateFactsWithBlankKey() {
        Map<String, Object> values = new HashMap<>();
        values.put("","country");
        assertThrows(IllegalArgumentException.class, () -> new Facts(values));
    }

    @Test
    void shouldRejectCreateFactsWithNullValue() {
        Map<String, Object> values = new HashMap<>();
        values.put("amount", null);
        assertThrows(IllegalArgumentException.class, () -> new Facts(values));
    }

    @Test
    void shouldProtectFactsFromExternalMapChanges() {

        Map<String, Object> values =
                new HashMap<>();

        values.put("amount", 15000);

        Facts facts =
                new Facts(values);

        values.put("amount", 500);

        assertEquals(
                15000,
                facts.get("amount")
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "  "})
    void shouldRejectGetFactsWithEmptyAndNullFactKey(String invalidFactKey) {
        Map<String, Object> values = Map.of(
                "amount", 15000
        );
        Facts facts = new Facts(values);
        assertThrows(IllegalArgumentException.class, () -> facts.get(invalidFactKey));
    }

    @Test
    void shouldRejectGetFactsWithoutFactKey() {
        Map<String, Object> values = Map.of(
                "amount", 15000

        );


        Facts facts = new Facts(values);
        assertThrows(IllegalStateException.class, () -> facts.get("country"));
    }

}