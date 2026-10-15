package com.sdney.rulesengine.domain.evaluations.comparison;

import com.sdney.rulesengine.domain.rule.ComparisonOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GreaterThanOrEqualsStrategyTest {

    @Test
    void shouldReturnGreaterThanOrEquals() {
        GreaterThanOrEqualsStrategy greaterThanOrEqualsStrategy = new GreaterThanOrEqualsStrategy();
        ComparisonOperator results = greaterThanOrEqualsStrategy.operator();
        assertEquals(results, ComparisonOperator.GREATER_THAN_OR_EQUALS);
    }

    @ParameterizedTest
    @MethodSource("greaterThanOrEqualsScenarios")
    void shouldEvaluateGreaterThanOrEquals(Object actualValue, Object expectedValue, boolean expectedResult) {

        GreaterThanOrEqualsStrategy greaterThanOrEqualsStrategy = new GreaterThanOrEqualsStrategy();
        boolean result = greaterThanOrEqualsStrategy.evaluate(actualValue, expectedValue);

        assertEquals(result, expectedResult);
    }

    private static Stream<Arguments> greaterThanOrEqualsScenarios() {
        return Stream.of(
                Arguments.of(1000, 100, true),
                Arguments.of(100, 1000, false),
                Arguments.of(1000, 1000, true)
        );
    }

    @Test
    void shouldRejectGreaterThanOrEqualsEvaluate () {
        GreaterThanOrEqualsStrategy greaterThanOrEqualsStrategy = new GreaterThanOrEqualsStrategy();
        Object actualValue = new Object();
        Object expectedValue = 1000;

        assertThrows(IllegalArgumentException.class, () -> greaterThanOrEqualsStrategy.evaluate(actualValue, expectedValue));
    }



}