package com.sdney.rulesengine.domain.evaluations.comparison;

import com.sdney.rulesengine.domain.rule.ComparisonOperator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InStrategyTest {

    @Test
    void shouldReturnInOperator() {
        InStrategy inStrategy = new InStrategy();
        ComparisonOperator result = inStrategy.operator();
        assertEquals(ComparisonOperator.IN, result);
    }

    @Test
    void shouldEvaluateIn() {
        InStrategy inStrategy = new InStrategy();
        Object actualValue = "BR";
        Object expectedValue = List.of(
                "BR",
                "ANG",
                "USA"
        );

        boolean result = inStrategy.evaluate(actualValue, expectedValue);
        assertTrue(result);
    }

    @Test
    void shouldRejectEvaluateIn() {
        InStrategy inStrategy = new InStrategy();
        Object actualValue = "BD";
        Object expectedValue = List.of(
                "BR",
                "ANG",
                "USA"
        );

        boolean result = inStrategy.evaluate(actualValue, expectedValue);
        assertTrue(!result);
    }

    @Test
    void shouldRejectEvaluateExpectedValueNotCollection() {
        InStrategy inStrategy = new InStrategy();
        Object actualValue = "BR";
        Object expectedValue = 10;
        assertThrows(IllegalArgumentException.class, ()-> inStrategy.evaluate(actualValue, expectedValue));
    }

}