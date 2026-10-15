package com.sdney.rulesengine.domain.evaluations;

import com.sdney.rulesengine.domain.rule.ConditionGroup;
import com.sdney.rulesengine.domain.rule.LogicalOperator;
import com.sdney.rulesengine.domain.rule.RuleCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupEvaluatorTest {

    @Mock
    private ConditionEvaluator conditionEvaluator;

    @Mock
    private Facts facts;

    @Mock
    private RuleCondition firstCondition;

    @Mock
    private RuleCondition secondCondition;

    @Mock
    private RuleCondition thirdCondition;

    private GroupEvaluator groupEvaluator;

    @BeforeEach
    void setUp() {
        groupEvaluator =
                new GroupEvaluator(conditionEvaluator);
    }

    @Test
    void shouldReturnTrueWhenAllConditionsMatchWithAndOperator() {

        ConditionGroup group =
                new ConditionGroup(LogicalOperator.AND);

        group.addCondition(firstCondition);
        group.addCondition(secondCondition);

        when(conditionEvaluator.evaluate(
                firstCondition,
                facts
        )).thenReturn(true);

        when(conditionEvaluator.evaluate(
                secondCondition,
                facts
        )).thenReturn(true);

        boolean result =
                groupEvaluator.evaluate(
                        group,
                        facts
                );

        assertTrue(result);

        verify(conditionEvaluator)
                .evaluate(firstCondition, facts);

        verify(conditionEvaluator)
                .evaluate(secondCondition, facts);
    }

    @Test
    void shouldReturnFalseWhenOneConditionDoesNotMatchWithAndOperator() {

        ConditionGroup group =
                new ConditionGroup(LogicalOperator.AND);

        group.addCondition(firstCondition);
        group.addCondition(secondCondition);

        when(conditionEvaluator.evaluate(
                firstCondition,
                facts
        )).thenReturn(true);

        when(conditionEvaluator.evaluate(
                secondCondition,
                facts
        )).thenReturn(false);

        boolean result =
                groupEvaluator.evaluate(
                        group,
                        facts
                );

        assertFalse(result);
    }

    @Test
    void shouldStopEvaluatingAndGroupAfterFirstFalseCondition() {

        ConditionGroup group =
                new ConditionGroup(LogicalOperator.AND);

        group.addCondition(firstCondition);
        group.addCondition(secondCondition);
        group.addCondition(thirdCondition);

        when(conditionEvaluator.evaluate(
                firstCondition,
                facts
        )).thenReturn(true);

        when(conditionEvaluator.evaluate(
                secondCondition,
                facts
        )).thenReturn(false);

        boolean result =
                groupEvaluator.evaluate(
                        group,
                        facts
                );

        assertFalse(result);

        verify(conditionEvaluator)
                .evaluate(firstCondition, facts);

        verify(conditionEvaluator)
                .evaluate(secondCondition, facts);

        verify(conditionEvaluator, never())
                .evaluate(thirdCondition, facts);
    }

    @Test
    void shouldReturnTrueWhenOneConditionMatchesWithOrOperator() {

        ConditionGroup group =
                new ConditionGroup(LogicalOperator.OR);

        group.addCondition(firstCondition);
        group.addCondition(secondCondition);

        when(conditionEvaluator.evaluate(
                firstCondition,
                facts
        )).thenReturn(false);

        when(conditionEvaluator.evaluate(
                secondCondition,
                facts
        )).thenReturn(true);

        boolean result =
                groupEvaluator.evaluate(
                        group,
                        facts
                );

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenAllConditionsDoNotMatchWithOrOperator() {

        ConditionGroup group =
                new ConditionGroup(LogicalOperator.OR);

        group.addCondition(firstCondition);
        group.addCondition(secondCondition);

        when(conditionEvaluator.evaluate(
                firstCondition,
                facts
        )).thenReturn(false);

        when(conditionEvaluator.evaluate(
                secondCondition,
                facts
        )).thenReturn(false);

        boolean result =
                groupEvaluator.evaluate(
                        group,
                        facts
                );

        assertFalse(result);
    }

    @Test
    void shouldStopEvaluatingOrGroupAfterFirstTrueCondition() {

        ConditionGroup group =
                new ConditionGroup(LogicalOperator.OR);

        group.addCondition(firstCondition);
        group.addCondition(secondCondition);
        group.addCondition(thirdCondition);

        when(conditionEvaluator.evaluate(
                firstCondition,
                facts
        )).thenReturn(false);

        when(conditionEvaluator.evaluate(
                secondCondition,
                facts
        )).thenReturn(true);

        boolean result =
                groupEvaluator.evaluate(
                        group,
                        facts
                );

        assertTrue(result);

        verify(conditionEvaluator)
                .evaluate(firstCondition, facts);

        verify(conditionEvaluator)
                .evaluate(secondCondition, facts);

        verify(conditionEvaluator, never())
                .evaluate(thirdCondition, facts);
    }

    @Test
    void shouldRejectNullConditionEvaluator() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new GroupEvaluator(null)
        );
    }

    @Test
    void shouldRejectNullConditionGroup() {

        assertThrows(
                IllegalArgumentException.class,
                () -> groupEvaluator.evaluate(
                        null,
                        facts
                )
        );
    }

    @Test
    void shouldRejectNullFacts() {

        ConditionGroup group =
                new ConditionGroup(LogicalOperator.AND);

        group.addCondition(firstCondition);

        assertThrows(
                IllegalArgumentException.class,
                () -> groupEvaluator.evaluate(
                        group,
                        null
                )
        );
    }

    @Test
    void shouldRejectEmptyConditionGroup() {

        ConditionGroup group =
                new ConditionGroup(LogicalOperator.AND);

        assertThrows(
                IllegalStateException.class,
                () -> groupEvaluator.evaluate(
                        group,
                        facts
                )
        );
    }
}