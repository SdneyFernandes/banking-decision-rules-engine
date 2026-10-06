package com.sdney.rulesengine.domain.rule;

import org.junit.jupiter.api.Test;

import static com.sdney.rulesengine.domain.rule.ComparisonOperator.GREATER_THAN;
import static com.sdney.rulesengine.domain.rule.LogicalOperator.AND;
import static com.sdney.rulesengine.domain.rule.ValueType.DECIMAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConditionGroupTest {

    @Test
    void shouldCreateConditionGroupWithValidOperator() {
        ConditionGroup group = new ConditionGroup(AND);

        assertEquals(AND, group.getLogicalOperator());
        assertTrue(group.getConditions().isEmpty());
    }

    @Test
    void shouldRejectNullLogicalOperator() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ConditionGroup(null)
        );
    }

    @Test
    void shouldAddCondition() {
        ConditionGroup group = new ConditionGroup(AND);
        RuleCondition condition = validCondition();

        group.addCondition(condition);

        assertEquals(1, group.getConditions().size());
        assertTrue(group.getConditions().contains(condition));
    }

    @Test
    void shouldRejectNullCondition() {
        ConditionGroup group = new ConditionGroup(AND);

        assertThrows(
                IllegalArgumentException.class,
                () -> group.addCondition(null)
        );
    }

    @Test
    void shouldExposeConditionsAsImmutableList() {
        ConditionGroup group = new ConditionGroup(AND);
        RuleCondition condition = validCondition();

        group.addCondition(condition);

        assertThrows(
                UnsupportedOperationException.class,
                () -> group.getConditions().add(validCondition())
        );
    }

    private RuleCondition validCondition() {
        return new RuleCondition(
                "amount",
                GREATER_THAN,
                DECIMAL,
                "10000"
        );
    }
}
