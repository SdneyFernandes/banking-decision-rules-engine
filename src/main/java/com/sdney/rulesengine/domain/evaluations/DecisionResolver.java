package com.sdney.rulesengine.domain.evaluations;

import com.sdney.rulesengine.domain.rule.RuleDefinition;

import java.util.Optional;

public class DecisionResolver {

    public Decision resolve(
            Optional<RuleDefinition> matchedRule
    ) {

        if (matchedRule == null) {
            throw new IllegalArgumentException(
                    "Matched rule result is required"
            );
        }

        return matchedRule
                .map(Decision::match)
                .orElseGet(Decision::noMatch);
    }
}
