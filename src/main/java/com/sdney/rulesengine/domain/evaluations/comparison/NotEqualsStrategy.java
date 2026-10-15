package com.sdney.rulesengine.domain.evaluations.comparison;

import com.sdney.rulesengine.domain.evaluations.ComparisonStrategy;
import com.sdney.rulesengine.domain.rule.ComparisonOperator;

import java.util.Objects;

public class NotEqualsStrategy implements ComparisonStrategy {

    @Override
    public ComparisonOperator operator() {
        return ComparisonOperator.NOT_EQUALS;
    }

    @Override
    public boolean evaluate(Object expectedValue, Object actualValue) {
        return !Objects.equals(expectedValue, actualValue);
    }
}
