package com.sdney.rulesengine.domain.evaluations;

import com.sdney.rulesengine.domain.rule.RuleDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class DecisionTest {

    @Test
    void shouldCreateMatchedDecision() {

        RuleDefinition rule =
                mock(RuleDefinition.class);

        Decision decision =
                Decision.match(rule);

        assertAll(
                () -> assertTrue(
                        decision.isMatched()
                ),
                () -> assertSame(
                        rule,
                        decision.getMatchedRule()
                )
        );
    }

    @Test
    void shouldCreateNoMatchDecision() {

        Decision decision =
                Decision.noMatch();

        assertAll(
                () -> assertFalse(
                        decision.isMatched()
                ),
                () -> assertNull(
                        decision.getMatchedRule()
                )
        );
    }

    @Test
    void shouldRejectMatchWithNullRule() {

        assertThrows(
                IllegalArgumentException.class,
                () -> Decision.match(null)
        );
    }
}