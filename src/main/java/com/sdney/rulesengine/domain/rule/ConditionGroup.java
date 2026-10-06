package com.sdney.rulesengine.domain.rule;

import java.util.ArrayList;
import java.util.List;

public class ConditionGroup {

    private final LogicalOperator logicalOperator;
    private final List<RuleCondition> conditions = new ArrayList<>();

    public ConditionGroup(LogicalOperator logicalOperator) {

        if (logicalOperator == null) {
            throw new IllegalArgumentException("LogicalOperator is required");
        }
        this.logicalOperator = logicalOperator;
    }

    public void addCondition(RuleCondition condition) {

        if(condition == null){
            throw new IllegalArgumentException("Condition is required");
        }
        conditions.add(condition);
    }

    public LogicalOperator getLogicalOperator() {
        return logicalOperator;
    }

    public List<RuleCondition> getConditions() {
        return List.copyOf(conditions);
    }
}
