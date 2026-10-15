package com.sdney.rulesengine.domain.evaluations.comparison;

import com.sdney.rulesengine.domain.rule.ComparisonOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EqualsStrategyTest {

    @Test
    void shouldReturnEqualsOperator() {
        EqualsStrategy strategy = new EqualsStrategy();
        ComparisonOperator result = strategy.operator();

        assertEquals(ComparisonOperator.EQUALS, result);
    }

    @ParameterizedTest
    @MethodSource("equalityScenarios")
    void shouldEvaluateEquality(Object expectedValue, Object actualValue, boolean expectedResult) {
        EqualsStrategy strategy = new EqualsStrategy();
        boolean result = strategy.evaluate(expectedValue, actualValue);
        assertEquals(expectedResult, result);

    }

    private static Stream<Arguments> equalityScenarios() {
        return Stream.of(
                Arguments.of(1000, 1000, true),
                Arguments.of(1000, 2000, false),
                Arguments.of("BR", "BR", true),
                Arguments.of("BR", "AO", false),
                Arguments.of(null, null, true),
                Arguments.of(null, "BR", false),
                Arguments.of("BR", null, false)
        );
    }
}