package com.sdney.rulesengine.domain.evaluations.comparison;

import com.sdney.rulesengine.domain.rule.ComparisonOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class NotEqualsStrategyTest {

    @Test
    void shouldReturnNotEqualsOperator() {
        NotEqualsStrategy notEqualsStrategy = new NotEqualsStrategy();
        ComparisonOperator result = notEqualsStrategy.operator();

        assertEquals(ComparisonOperator.NOT_EQUALS, result);
    }

    @ParameterizedTest
    @MethodSource("notEqualityScenarios")
    void shouldEvaluateNotEquality(Object expectedValue, Object actualValue, boolean expectedResult) {
        NotEqualsStrategy notEqualsStrategy = new NotEqualsStrategy();
        boolean result = notEqualsStrategy.evaluate(expectedValue, actualValue);
        assertEquals(expectedResult, result);
    }

    private static Stream<Arguments> notEqualityScenarios() {
        return Stream.of(
                Arguments.of(1000, 1000, false),
                Arguments.of(1000, 2000, true),
                Arguments.of("BR", "BR", false),
                Arguments.of("BR", "AO", true),
                Arguments.of(null, null, false),
                Arguments.of(null, "BR", true),
                Arguments.of("BR", null, true)
        );
    }
}