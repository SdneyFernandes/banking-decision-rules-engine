package com.sdney.rulesengine.domain.evaluations;

import com.sdney.rulesengine.domain.rule.ComparisonOperator;

public interface ComparisonStrategy {

    ComparisonOperator operator();

    boolean evaluate(
            Object actualValue,
            Object expectedValue
    );
}



