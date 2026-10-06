package com.sdney.rulesengine.domain.rule;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConditionGroupTest {

    @Test
    void shouldCreateConditionGroupWithValidData() {

        LogicalOperator operator = LogicalOperator.AND;
        ConditionGroup conditionGroup = new ConditionGroup(operator);

        assertEquals(operator, conditionGroup.getLogicalOperator());

        assertTrue(conditionGroup.getConditions().isEmpty());
    }

    @Test
    void sholdAddConditionInConditionGroup() {
        LogicalOperator logicalOperator = LogicalOperator.AND;

        String factKey = "amount";
        ComparisonOperator operator = ComparisonOperator.GREATER_THAN;
        ValueType valueType = ValueType.DECIMAL;
        String factValue = "10000";

        RuleCondition ruleCondition = new RuleCondition(factKey, operator, valueType, factValue);
        ConditionGroup conditionGroup = new ConditionGroup(logicalOperator);
        conditionGroup.addCondition(ruleCondition);
        assertEquals(1,conditionGroup.getConditions().size());


    }

    @Test
    void shouldRejectCreateConditionGroupWithInvalidData() {

        LogicalOperator operator = null;

       assertThrows(IllegalArgumentException.class, () -> new ConditionGroup(operator));
    }

    @Test
    void sholdeRejectAddConditionInConditionGroup() {
        LogicalOperator logicalOperator = LogicalOperator.AND;
        RuleCondition ruleCondition = null;

        ConditionGroup conditionGroup = new ConditionGroup(logicalOperator);
        assertThrows(IllegalArgumentException.class, () -> conditionGroup.addCondition(ruleCondition));


    }

}