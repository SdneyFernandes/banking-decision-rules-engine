package com.sdney.rulesengine.domain.rule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.sdney.rulesengine.domain.rule.ComparisonOperator.GREATER_THAN;
import static com.sdney.rulesengine.domain.rule.ValueType.DECIMAL;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RuleConditionTest {

    @Test
    void shouldCreateRuleConditionWithValidData() {
        RuleCondition condition = new RuleCondition(
                "amount",
                GREATER_THAN,
                DECIMAL,
                "10000"
        );

        assertAll(
                () -> assertEquals(
                        "amount",
                        condition.getFactKey()
                ),
                () -> assertEquals(
                        GREATER_THAN,
                        condition.getOperator()
                ),
                () -> assertEquals(
                        DECIMAL,
                        condition.getValueType()
                ),
                () -> assertEquals(
                        "10000",
                        condition.getExpectedValue()
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
                        GREATER_THAN,
                        DECIMAL,
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
                        DECIMAL,
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
                        GREATER_THAN,
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
                        GREATER_THAN,
                        DECIMAL,
                        invalidExpectedValue
                )
        );
    }
}
