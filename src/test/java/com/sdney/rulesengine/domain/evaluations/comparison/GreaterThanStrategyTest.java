package com.sdney.rulesengine.domain.evaluations.comparison;

import com.sdney.rulesengine.domain.rule.ComparisonOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GreaterThanStrategyTest {

    @Test
    void shouldReturnGreaterThanOperator () {
        GreaterThanStrategy greaterThanStrategy = new GreaterThanStrategy();
        ComparisonOperator result = greaterThanStrategy.operator();
        assertEquals(result, ComparisonOperator.GREATER_THAN);
    }

    @ParameterizedTest
    @MethodSource("greaterThanScenarios")
    void shouldEvaluateGreaterThan(Object actualValue, Object expectedValue, boolean expectedResult) {

        GreaterThanStrategy greaterThanStrategy = new GreaterThanStrategy();
        boolean result = greaterThanStrategy.evaluate(actualValue, expectedValue);

        assertEquals(result, expectedResult);
    }

    private static Stream<Arguments> greaterThanScenarios() {
        return Stream.of(
                Arguments.of(1000, 100, true),
                Arguments.of(100, 1000, false),
                Arguments.of(1000, 1000, false)
        );
    }

    @Test
    void shouldRejectGreaterThanEvaluate () {
        GreaterThanStrategy greaterThanStrategy = new GreaterThanStrategy();
        Object actualValue = new Object();
        Object expectedValue = 1000;

        assertThrows(IllegalArgumentException.class, () -> greaterThanStrategy.evaluate(actualValue, expectedValue));
    }

}