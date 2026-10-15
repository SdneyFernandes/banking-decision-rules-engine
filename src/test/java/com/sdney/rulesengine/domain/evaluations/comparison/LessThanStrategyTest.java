package com.sdney.rulesengine.domain.evaluations.comparison;

import com.sdney.rulesengine.domain.rule.ComparisonOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LessThanStrategyTest {

    @Test
    void shouldReturnLessThan() {
        LessThanStrategy lessThanStrategy = new LessThanStrategy();
        ComparisonOperator result = lessThanStrategy.operator();
        assertEquals(ComparisonOperator.LESS_THAN, result);
    }

    @ParameterizedTest
    @MethodSource("lessThanScenarios")
    void shouldEvaluateLessThan(Object actualValue, Object expectedValue, boolean expectedResult) {

        LessThanStrategy lessThanStrategy = new LessThanStrategy();
        boolean result = lessThanStrategy.evaluate(actualValue, expectedValue);

        assertEquals(result, expectedResult);
    }

    private static Stream<Arguments> lessThanScenarios() {
        return Stream.of(
                Arguments.of(1000, 100, false),
                Arguments.of(100, 1000, true),
                Arguments.of(1000, 1000, false)
        );
    }

    @Test
    void shouldRejectLessThanEvaluate () {
        LessThanStrategy lessThanStrategy = new LessThanStrategy();
        Object actualValue = new Object();
        Object expectedValue = 1000;

        assertThrows(IllegalArgumentException.class, () -> lessThanStrategy.evaluate(actualValue, expectedValue));
    }

}