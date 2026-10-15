package com.sdney.rulesengine.domain.evaluations.comparison;

import com.sdney.rulesengine.domain.evaluations.ComparisonStrategy;
import com.sdney.rulesengine.domain.rule.ComparisonOperator;

import java.util.Collection;

public class InStrategy implements ComparisonStrategy {

    @Override
    public ComparisonOperator operator() {
        return ComparisonOperator.IN;
    }

    @Override
    public boolean evaluate(Object actualValue, Object expectedValue) {

        if (!(expectedValue instanceof Collection<?> values)) {
            throw new IllegalArgumentException("Expected value must be a collection");
        }
        return values.contains(actualValue);
    }
}
