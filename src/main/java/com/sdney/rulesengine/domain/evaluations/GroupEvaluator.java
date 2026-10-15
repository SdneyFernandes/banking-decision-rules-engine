package com.sdney.rulesengine.domain.evaluations;

import com.sdney.rulesengine.domain.rule.ConditionGroup;

public class GroupEvaluator {

    private final ConditionEvaluator conditionEvaluator;

    public GroupEvaluator(ConditionEvaluator conditionEvaluator) {
        if (conditionEvaluator == null) {
            throw new IllegalArgumentException("Condition evaluator is required");
        }
        this.conditionEvaluator = conditionEvaluator;
    }

    public boolean evaluate(ConditionGroup group, Facts facts) {

        if (group == null) {
            throw new IllegalArgumentException("Condition group is required");
        }

        if (facts == null) {
            throw new IllegalArgumentException("Facts are required");
        }

        if (group.getConditions().isEmpty()) {
            throw new IllegalStateException("Condition group cannot be empty");
        }

        return switch (group.getLogicalOperator()) {
            case AND ->
                    evaluateAnd(group, facts);
            case OR ->
                    evaluateOr(group, facts);
        };
    }

    private boolean evaluateAnd(ConditionGroup group, Facts facts) {

        for (var condition : group.getConditions()) {
            boolean result = conditionEvaluator.evaluate(
                            condition,
                            facts
                    );

            if (!result) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateOr(ConditionGroup group, Facts facts) {
        for (var condition : group.getConditions()) {
            boolean result =
                    conditionEvaluator.evaluate(
                            condition,
                            facts
                    );
            if (result) {
                return true;
            }
        }

        return false;
    }
}
