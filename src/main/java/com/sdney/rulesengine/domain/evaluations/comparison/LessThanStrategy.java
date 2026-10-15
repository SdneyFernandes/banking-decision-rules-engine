package com.sdney.rulesengine.domain.evaluations.comparison;

import com.sdney.rulesengine.domain.evaluations.ComparisonStrategy;
import com.sdney.rulesengine.domain.rule.ComparisonOperator;

public class LessThanStrategy implements ComparisonStrategy {

    @Override
    public ComparisonOperator operator() {
        return ComparisonOperator.LESS_THAN;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean evaluate(Object actualValue, Object expectedValue) {

        if (!(actualValue instanceof Comparable<?>)) {
            throw new IllegalArgumentException("Actual value is not comparable");
        }

        Comparable<Object> actual = (Comparable<Object>) actualValue;
        return actual.compareTo(expectedValue) < 0;
    }
}
