package com.sdney.rulesengine.infrastructure.database.entity;

import com.sdney.rulesengine.domain.rule.LogicalOperator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "condition_group")
public class ConditionGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
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

    public ConditionGroupEntity(
            RuleDefinitionEntity ruleDefinition,
            LogicalOperator logicalOperator
    ) {
        this.ruleDefinition = ruleDefinition;
        this.logicalOperator = logicalOperator;
    }

    protected ConditionGroupEntity() {
    }

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
