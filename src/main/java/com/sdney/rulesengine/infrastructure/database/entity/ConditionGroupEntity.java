package com.sdney.rulesengine.infrastructure.database.entity;


import com.sdney.rulesengine.domain.rule.LogicalOperator;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "condition_group")
public class ConditionGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "rule_definition_id",
            nullable = false
    )
    private RuleDefinitionEntity ruleDefinition;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "logical_operator",
            nullable = false,
            length = 10
    )
    private LogicalOperator logicalOperator;

    @OneToMany(
            mappedBy = "conditionGroup",
            fetch = FetchType.LAZY
    )
    private List<RuleConditionEntity> conditions = new ArrayList<>();

    public ConditionGroupEntity(RuleDefinitionEntity ruleDefinition, LogicalOperator logicalOperator, List<RuleConditionEntity> conditions) {
        this.ruleDefinition = ruleDefinition;
        this.logicalOperator = logicalOperator;
        this.conditions = conditions;
    }

    protected ConditionGroupEntity() {}

    public Long getId() {
        return id;
    }

    public RuleDefinitionEntity getRuleDefinition() {
        return ruleDefinition;
    }

    public LogicalOperator getLogicalOperator() {
        return logicalOperator;
    }

    public List<RuleConditionEntity> getConditions() {
        return conditions;
    }
}
