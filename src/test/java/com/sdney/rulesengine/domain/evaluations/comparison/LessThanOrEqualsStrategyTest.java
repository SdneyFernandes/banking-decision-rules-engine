package com.sdney.rulesengine.domain.evaluations.comparison;

import com.sdney.rulesengine.domain.rule.ComparisonOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LessThanOrEqualsStrategyTest {

    @Test
    void shouldReturnLessThanOrEquals() {
        LessThanOrEqualsStrategy lessThanOrEqualsStrategy = new LessThanOrEqualsStrategy();
        ComparisonOperator result = lessThanOrEqualsStrategy.operator();
        assertEquals(ComparisonOperator.LESS_THAN_OR_EQUALS, result);
    }

    @ParameterizedTest
    @MethodSource("lessThanOrEqualsScenarios")
    void shouldEvaluateLessThanOrEquals(Object actualValue, Object expectedValue, boolean expectedResult) {

        LessThanOrEqualsStrategy lessThanOrEqualsStrategy = new LessThanOrEqualsStrategy();
        boolean result = lessThanOrEqualsStrategy.evaluate(actualValue, expectedValue);

        assertEquals(result, expectedResult);
    }

    private static Stream<Arguments> lessThanOrEqualsScenarios() {
        return Stream.of(
                Arguments.of(1000, 100, false),
                Arguments.of(100, 1000, true),
                Arguments.of(1000, 1000, true)
        );
    }

    @Test
    void shouldRejectLessThanOrEqualsEvaluate () {
        LessThanOrEqualsStrategy lessThanOrEqualsStrategy = new LessThanOrEqualsStrategy();
        Object actualValue = new Object();
        Object expectedValue = 1000;

        assertThrows(IllegalArgumentException.class, () -> lessThanOrEqualsStrategy.evaluate(actualValue, expectedValue));
    }


}