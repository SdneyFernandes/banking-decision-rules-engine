package com.sdney.rulesengine.domain.evaluations;

import com.sdney.rulesengine.domain.rule.ConditionGroup;
import com.sdney.rulesengine.domain.rule.RuleDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RuleEvaluatorTest {

    @Mock
    private GroupEvaluator groupEvaluator;

    @Mock
    private Facts facts;

    @Mock
    private RuleDefinition rule;

    @Mock
    private ConditionGroup firstGroup;

    @Mock
    private ConditionGroup secondGroup;

    @Mock
    private ConditionGroup thirdGroup;

    private RuleEvaluator ruleEvaluator;

    @BeforeEach
    void setUp() {
        ruleEvaluator =
                new RuleEvaluator(groupEvaluator);
    }

    @Test
    void shouldReturnTrueWhenAllGroupsMatch() {

        when(rule.getConditionGroups())
                .thenReturn(
                        List.of(
                                firstGroup,
                                secondGroup
                        )
                );

        when(groupEvaluator.evaluate(
                firstGroup,
                facts
        )).thenReturn(true);

        when(groupEvaluator.evaluate(
                secondGroup,
                facts
        )).thenReturn(true);

        boolean result =
                ruleEvaluator.evaluate(
                        rule,
                        facts
                );

        assertTrue(result);

        verify(groupEvaluator)
                .evaluate(firstGroup, facts);

        verify(groupEvaluator)
                .evaluate(secondGroup, facts);
    }

    @Test
    void shouldReturnFalseWhenOneGroupDoesNotMatch() {

        when(rule.getConditionGroups())
                .thenReturn(
                        List.of(
                                firstGroup,
                                secondGroup
                        )
                );

        when(groupEvaluator.evaluate(
                firstGroup,
                facts
        )).thenReturn(true);

        when(groupEvaluator.evaluate(
                secondGroup,
                facts
        )).thenReturn(false);

        boolean result =
                ruleEvaluator.evaluate(
                        rule,
                        facts
                );

        assertFalse(result);
    }

    @Test
    void shouldStopEvaluatingAfterFirstFalseGroup() {

        when(rule.getConditionGroups())
                .thenReturn(
                        List.of(
                                firstGroup,
                                secondGroup,
                                thirdGroup
                        )
                );

        when(groupEvaluator.evaluate(
                firstGroup,
                facts
        )).thenReturn(true);

        when(groupEvaluator.evaluate(
                secondGroup,
                facts
        )).thenReturn(false);

        boolean result =
                ruleEvaluator.evaluate(
                        rule,
                        facts
                );

        assertFalse(result);

        verify(groupEvaluator)
                .evaluate(firstGroup, facts);

        verify(groupEvaluator)
                .evaluate(secondGroup, facts);

        verify(groupEvaluator, never())
                .evaluate(thirdGroup, facts);
    }

    @Test
    void shouldRejectNullGroupEvaluator() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new RuleEvaluator(null)
        );
    }

    @Test
    void shouldRejectNullRule() {

        assertThrows(
                IllegalArgumentException.class,
                () -> ruleEvaluator.evaluate(
                        null,
                        facts
                )
        );
    }

    @Test
    void shouldRejectNullFacts() {

        assertThrows(
                IllegalArgumentException.class,
                () -> ruleEvaluator.evaluate(
                        rule,
                        null
                )
        );
    }

    @Test
    void shouldRejectRuleWithoutConditionGroups() {

        when(rule.getConditionGroups())
                .thenReturn(List.of());

        assertThrows(
                IllegalStateException.class,
                () -> ruleEvaluator.evaluate(
                        rule,
                        facts
                )
        );
    }
}