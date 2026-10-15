package com.sdney.rulesengine.domain.evaluations;

import com.sdney.rulesengine.domain.evaluations.ComparisonStrategy;
import com.sdney.rulesengine.domain.evaluations.OperatorResolver;
import com.sdney.rulesengine.domain.rule.RuleCondition;

public class ConditionEvaluator {

    private final ValueConverter valueConverter;
    private final OperatorResolver operatorResolver;

    public ConditionEvaluator(ValueConverter valueConverter, OperatorResolver operatorResolver) {
        if (valueConverter == null) {
            throw new IllegalArgumentException("Value converter is required");
        }

        if (operatorResolver == null) {
            throw new IllegalArgumentException("Operator resolver is required");
        }

        this.valueConverter = valueConverter;
        this.operatorResolver = operatorResolver;
    }

    public boolean evaluate(RuleCondition condition, Facts facts) {

        if (condition == null) {
            throw new IllegalArgumentException("Rule condition is required");
        }

        if (facts == null) {
            throw new IllegalArgumentException("Facts are required");
        }

        Object actualValue = facts.get(condition.getFactKey());

        Object convertedActual = valueConverter.convert(actualValue, condition.getValueType());

        Object convertedExpected = convertExpectedValue(condition);

        ComparisonStrategy strategy = operatorResolver.resolve(condition.getOperator());

        return strategy.evaluate(convertedActual, convertedExpected);
    }

    private Object convertExpectedValue(RuleCondition condition) {

        return switch (condition.getOperator()) {
            case IN, NOT_IN ->
                    valueConverter.convertList(
                            condition.getExpectedValue(),
                            condition.getValueType()
                    );
            default ->
                    valueConverter.convert(
                            condition.getExpectedValue(),
                            condition.getValueType()
                    );
        };
    }
}