package com.sdney.rulesengine.domain.rule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;


class RuleConditionTest {

    @Test
    void shouldCreateRuleConditionWithValidData() {

        String factKey = "amount";
        ComparisonOperator operator = ComparisonOperator.GREATER_THAN;
        ValueType valueType = ValueType.DECIMAL;
        String factValue = "10000";

        RuleCondition ruleCondition = new RuleCondition(factKey, operator, valueType, factValue);

        assertAll(
                ()-> assertEquals(factKey, ruleCondition.getFactKey()),
                ()-> assertEquals(factValue, ruleCondition.getExpectedValue()),
                ()-> assertEquals(operator, ruleCondition.getOperator()),
                ()-> assertEquals(valueType, ruleCondition.getValueType())

        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "  "})
    void shouldRejectInvalidFactKey(String invalidFactKey) {


        ComparisonOperator operator = ComparisonOperator.GREATER_THAN;
        ValueType valueType = ValueType.DECIMAL;
        String factValue = "10000";

        assertThrows(IllegalArgumentException.class,
                () -> {
            RuleCondition ruleCondition = new RuleCondition(invalidFactKey, operator, valueType, factValue);
                });
    }

}