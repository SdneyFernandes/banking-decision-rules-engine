package com.sdney.rulesengine.domain.evaluations;

import com.sdney.rulesengine.domain.rule.RuleDefinition;

public class Decision {

    private final boolean matched;
    private final RuleDefinition matchedRule;

    private Decision(
            boolean matched,
            RuleDefinition matchedRule
    ) {
        this.matched = matched;
        this.matchedRule = matchedRule;
    }

    public static Decision match(
            RuleDefinition rule
    ) {

        if (rule == null) {
            throw new IllegalArgumentException(
                    "Matched rule is required"
            );
        }

        return new Decision(
                true,
                rule
        );
    }

    public static Decision noMatch() {
        return new Decision(
                false,
                null
        );
    }

    public boolean isMatched() {
        return matched;
    }

    public RuleDefinition getMatchedRule() {
        return matchedRule;
    }
}
