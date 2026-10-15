package com.sdney.rulesengine.domain.evaluations;

import com.sdney.rulesengine.domain.rule.ComparisonOperator;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class OperatorResolver {

    private final Map<ComparisonOperator, ComparisonStrategy> strategies;

    public OperatorResolver(List<ComparisonStrategy> strategies) {

        if (strategies == null || strategies.isEmpty()) {
            throw new IllegalArgumentException("Comparison strategies are required");
        }

        Map<ComparisonOperator, ComparisonStrategy> strategyMap = new EnumMap<>(ComparisonOperator.class);

        for (ComparisonStrategy strategy : strategies) {
            if (strategy == null) {
                throw new IllegalArgumentException("Comparison strategy cannot be null");
            }

            ComparisonStrategy previous = strategyMap.putIfAbsent(strategy.operator(), strategy);

            if (previous != null) {
                throw new IllegalStateException("Duplicate strategy for operator: " + strategy.operator()
                );
            }
        }

        this.strategies = Map.copyOf(strategyMap);
    }

    public ComparisonStrategy resolve(ComparisonOperator operator) {

        if (operator == null) {
            throw new IllegalArgumentException("Comparison operator is required");
        }
        ComparisonStrategy strategy = strategies.get(operator);

        if (strategy == null) {
            throw new IllegalStateException("No strategy found for operator: " + operator);
        }
        return strategy;
    }
}