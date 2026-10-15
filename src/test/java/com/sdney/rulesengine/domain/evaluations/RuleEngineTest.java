package com.sdney.rulesengine.domain.evaluations;

import com.sdney.rulesengine.domain.rule.RuleDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RuleEngineTest {

    @Mock
    private RuleEvaluator ruleEvaluator;

    @Mock
    private DecisionResolver decisionResolver;

    @Mock
    private Facts facts;

    @Mock
    private RuleDefinition highPriorityRule;

    @Mock
    private RuleDefinition mediumPriorityRule;

    @Mock
    private RuleDefinition lowPriorityRule;

    @Mock
    private Decision decision;

    private RuleEngine ruleEngine;

    @BeforeEach
    void setUp() {

        ruleEngine =
                new RuleEngine(
                        ruleEvaluator,
                        decisionResolver
                );
    }

    @Test
    void shouldEvaluateRulesByPriorityAndReturnFirstMatch() {

        when(highPriorityRule.getPriority())
                .thenReturn(1);

        when(mediumPriorityRule.getPriority())
                .thenReturn(5);

        when(lowPriorityRule.getPriority())
                .thenReturn(10);

        List<RuleDefinition> rules =
                List.of(
                        lowPriorityRule,
                        highPriorityRule,
                        mediumPriorityRule
                );

        when(ruleEvaluator.evaluate(
                highPriorityRule,
                facts
        )).thenReturn(false);

        when(ruleEvaluator.evaluate(
                mediumPriorityRule,
                facts
        )).thenReturn(true);

        when(decisionResolver.resolve(
                Optional.of(mediumPriorityRule)
        )).thenReturn(decision);

        Decision result =
                ruleEngine.evaluate(
                        rules,
                        facts
                );

        assertSame(
                decision,
                result
        );

        InOrder inOrder =
                inOrder(ruleEvaluator);

        inOrder.verify(ruleEvaluator)
                .evaluate(
                        highPriorityRule,
                        facts
                );

        inOrder.verify(ruleEvaluator)
                .evaluate(
                        mediumPriorityRule,
                        facts
                );

        verify(ruleEvaluator, never())
                .evaluate(
                        lowPriorityRule,
                        facts
                );

        verify(decisionResolver)
                .resolve(
                        Optional.of(
                                mediumPriorityRule
                        )
                );
    }

    @Test
    void shouldReturnNoMatchDecisionWhenNoRuleMatches() {

        when(highPriorityRule.getPriority())
                .thenReturn(1);

        when(mediumPriorityRule.getPriority())
                .thenReturn(5);

        List<RuleDefinition> rules =
                List.of(
                        mediumPriorityRule,
                        highPriorityRule
                );

        when(ruleEvaluator.evaluate(
                highPriorityRule,
                facts
        )).thenReturn(false);

        when(ruleEvaluator.evaluate(
                mediumPriorityRule,
                facts
        )).thenReturn(false);

        when(decisionResolver.resolve(
                Optional.empty()
        )).thenReturn(decision);

        Decision result =
                ruleEngine.evaluate(
                        rules,
                        facts
                );

        assertSame(
                decision,
                result
        );

        verify(ruleEvaluator)
                .evaluate(
                        highPriorityRule,
                        facts
                );

        verify(ruleEvaluator)
                .evaluate(
                        mediumPriorityRule,
                        facts
                );

        verify(decisionResolver)
                .resolve(
                        Optional.empty()
                );
    }

    @Test
    void shouldRejectNullRules() {

        assertThrows(
                IllegalArgumentException.class,
                () -> ruleEngine.evaluate(
                        null,
                        facts
                )
        );
    }

    @Test
    void shouldRejectEmptyRules() {

        assertThrows(
                IllegalArgumentException.class,
                () -> ruleEngine.evaluate(
                        List.of(),
                        facts
                )
        );
    }

    @Test
    void shouldRejectNullFacts() {

        assertThrows(
                IllegalArgumentException.class,
                () -> ruleEngine.evaluate(
                        List.of(highPriorityRule),
                        null
                )
        );
    }

    @Test
    void shouldRejectNullRuleEvaluator() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new RuleEngine(
                        null,
                        decisionResolver
                )
        );
    }

    @Test
    void shouldRejectNullDecisionResolver() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new RuleEngine(
                        ruleEvaluator,
                        null
                )
        );
    }
}