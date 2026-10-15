package com.sdney.rulesengine.domain.evaluations;

import com.sdney.rulesengine.domain.rule.RuleDefinition;

public class RuleEvaluator {

    private final GroupEvaluator groupEvaluator;

    public RuleEvaluator(
            GroupEvaluator groupEvaluator
    ) {
        if (groupEvaluator == null) {
            throw new IllegalArgumentException(
                    "Group evaluator is required"
            );
        }

        this.groupEvaluator = groupEvaluator;
    }

    public boolean evaluate(
            RuleDefinition rule,
            Facts facts
    ) {

        if (rule == null) {
            throw new IllegalArgumentException(
                    "Rule is required"
            );
        }

        if (facts == null) {
            throw new IllegalArgumentException(
                    "Facts are required"
            );
        }

        if (rule.getConditionGroups().isEmpty()) {
            throw new IllegalStateException(
                    "Rule must have at least one condition group"
            );
        }

        for (var group : rule.getConditionGroups()) {

            boolean result =
                    groupEvaluator.evaluate(
                            group,
                            facts
                    );

            if (!result) {
                return false;
            }
        }

        return true;
    }
}
