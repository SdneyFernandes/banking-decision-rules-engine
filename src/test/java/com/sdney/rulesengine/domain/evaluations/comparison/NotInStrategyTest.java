package com.sdney.rulesengine.domain.evaluations.comparison;

import com.sdney.rulesengine.domain.rule.ComparisonOperator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotInStrategyTest {

    @Test
    void shouldReturnNotInOperator() {
        NotInStrategy notInStrategy = new NotInStrategy();
        ComparisonOperator result = notInStrategy.operator();
        assertEquals(ComparisonOperator.NOT_IN, result);
    }

    @Test
    void shouldEvaluateNotIn() {
        NotInStrategy notInStrategy = new NotInStrategy();
        Object actualValue = "BD";
        Object expectedValue = List.of(
                "BR",
                "ANG",
                "USA"
        );

        boolean result = notInStrategy.evaluate(actualValue, expectedValue);
        assertTrue(result);
    }

    @Test
    void shouldRejectEvaluateIn() {
        NotInStrategy notInStrategy = new NotInStrategy();
        Object actualValue = "BR";
        Object expectedValue = List.of(
                "BR",
                "ANG",
                "USA"
        );

        boolean result = notInStrategy.evaluate(actualValue, expectedValue);
        assertTrue(!result);
    }

    @Test
    void shouldRejectEvaluateExpectedValueNotCollection() {
        NotInStrategy notInStrategy = new NotInStrategy();
        Object actualValue = "BR";
        Object expectedValue = 10;
        assertThrows(IllegalArgumentException.class, ()-> notInStrategy.evaluate(actualValue, expectedValue));
    }


}