package com.sdney.rulesengine.domain.evaluations;

import com.sdney.rulesengine.domain.rule.RuleDefinition;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class DecisionResolverTest {

    private final DecisionResolver resolver =
            new DecisionResolver();

    @Test
    void shouldResolveMatchedDecisionWhenRuleExists() {

        RuleDefinition rule =
                mock(RuleDefinition.class);

        Optional<RuleDefinition> matchedRule =
                Optional.of(rule);

        Decision result =
                resolver.resolve(matchedRule);

        assertAll(
                () -> assertTrue(
                        result.isMatched()
                ),
                () -> assertSame(
                        rule,
                        result.getMatchedRule()
                )
        );
    }

    @Test
    void shouldResolveNoMatchDecisionWhenRuleDoesNotExist() {

        Optional<RuleDefinition> matchedRule =
                Optional.empty();

        Decision result =
                resolver.resolve(matchedRule);

        assertAll(
                () -> assertFalse(
                        result.isMatched()
                ),
                () -> assertNull(
                        result.getMatchedRule()
                )
        );
    }

    @Test
    void shouldRejectNullMatchedRuleResult() {

        assertThrows(
                IllegalArgumentException.class,
                () -> resolver.resolve(null)
        );
    }
}