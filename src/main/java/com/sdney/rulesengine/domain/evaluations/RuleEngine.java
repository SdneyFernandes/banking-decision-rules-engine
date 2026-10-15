package com.sdney.rulesengine.domain.evaluations;

import com.sdney.rulesengine.domain.rule.RuleDefinition;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class RuleEngine {

    private final RuleEvaluator ruleEvaluator;
    private final DecisionResolver decisionResolver;

    public RuleEngine(
            RuleEvaluator ruleEvaluator,
            DecisionResolver decisionResolver
    ) {

        if (ruleEvaluator == null) {
            throw new IllegalArgumentException(
                    "Rule evaluator is required"
            );
        }

        if (decisionResolver == null) {
            throw new IllegalArgumentException(
                    "Decision resolver is required"
            );
        }

        this.ruleEvaluator = ruleEvaluator;
        this.decisionResolver = decisionResolver;
    }

    public Decision evaluate(
            List<RuleDefinition> rules,
            Facts facts
    ) {

        if (rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException(
                    "Rules are required"
            );
        }

        if (facts == null) {
            throw new IllegalArgumentException(
                    "Facts are required"
            );
        }

        Optional<RuleDefinition> matchedRule =
                rules.stream()
                        .sorted(
                                Comparator.comparingInt(
                                        RuleDefinition::getPriority
                                )
                        )
                        .filter(rule ->
                                ruleEvaluator.evaluate(
                                        rule,
                                        facts
                                )
                        )
                        .findFirst();

        return decisionResolver.resolve(
                matchedRule
        );
    }
}
