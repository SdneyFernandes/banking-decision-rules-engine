package com.sdney.rulesengine.domain.rule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RuleConditionTest {

    @Test
    void shouldCreateRuleConditionWithValidData() {
        String factKey = "amount";
        ComparisonOperator operator = ComparisonOperator.GREATER_THAN;
        ValueType valueType = ValueType.DECIMAL;
        String expectedValue = "10000";

        RuleCondition ruleCondition =
                new RuleCondition(
                        factKey,
                        operator,
                        valueType,
                        expectedValue
                );

        assertAll(
                () -> assertEquals(
                        factKey,
                        ruleCondition.getFactKey()
                ),
                () -> assertEquals(
                        operator,
                        ruleCondition.getOperator()
                ),
                () -> assertEquals(
                        valueType,
                        ruleCondition.getValueType()
                ),
                () -> assertEquals(
                        expectedValue,
                        ruleCondition.getExpectedValue()
                )
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "  "})
    void shouldRejectInvalidFactKey(String invalidFactKey) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RuleCondition(
                        invalidFactKey,
                        ComparisonOperator.GREATER_THAN,
                        ValueType.DECIMAL,
                        "10000"
                )
        );
    }

    @Test
    void shouldRejectNullComparisonOperator() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RuleCondition(
                        "amount",
                        null,
                        ValueType.DECIMAL,
                        "10000"
                )
        );
    }

    @Test
    void shouldRejectNullValueType() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RuleCondition(
                        "amount",
                        ComparisonOperator.GREATER_THAN,
                        null,
                        "10000"
                )
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "  "})
    void shouldRejectInvalidExpectedValue(
            String invalidExpectedValue
    ) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RuleCondition(
                        "amount",
                        ComparisonOperator.GREATER_THAN,
                        ValueType.DECIMAL,
                        invalidExpectedValue
                )
        );
    }
}
