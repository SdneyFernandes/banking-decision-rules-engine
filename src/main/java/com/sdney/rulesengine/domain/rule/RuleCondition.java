package com.sdney.rulesengine.domain.rule;

public class RuleCondition {

    private final String factKey;
    private final ComparisonOperator operator;
    private final ValueType valueType;
    private final String expectedValue;

    public RuleCondition(
            String factKey,
            ComparisonOperator operator,
            ValueType valueType,
            String expectedValue) {

        if (factKey == null || factKey.isBlank()) {
            throw new IllegalArgumentException("Fact Key is required");
        }

        if (operator == null) {
            throw new IllegalArgumentException("Comparison Operator is required");
        }

        if (valueType == null) {
            throw new IllegalArgumentException("Value Type is required");
        }

        if (expectedValue == null || expectedValue.isBlank()) {
            throw new IllegalArgumentException("Expected Value is required");
        }

        this.factKey = factKey;
        this.operator = operator;
        this.valueType = valueType;
        this.expectedValue = expectedValue;
    }

    public String getFactKey() {
        return factKey;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public String getExpectedValue() {
        return expectedValue;
    }


}
