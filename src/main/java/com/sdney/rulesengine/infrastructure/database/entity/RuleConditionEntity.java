package com.sdney.rulesengine.infrastructure.database.entity;

import com.sdney.rulesengine.domain.rule.ComparisonOperator;
import com.sdney.rulesengine.domain.rule.ValueType;
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
import jakarta.persistence.Table;

@Entity
@Table(name = "rule_condition")
public class RuleConditionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "condition_group_id",
            nullable = false
    )
    private ConditionGroupEntity conditionGroup;

    @Column(
            name = "fact_key",
            nullable = false,
            length = 100
    )
    private String factKey;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "operator",
            nullable = false,
            length = 30
    )
    private ComparisonOperator operator;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "value_type",
            nullable = false,
            length = 20
    )
    private ValueType valueType;

    @Column(
            name = "expected_value",
            nullable = false,
            length = 500
    )
    private String expectedValue;

    public RuleConditionEntity(
            ConditionGroupEntity conditionGroup,
            String factKey,
            ComparisonOperator operator,
            ValueType valueType,
            String expectedValue
    ) {
        this.conditionGroup = conditionGroup;
        this.factKey = factKey;
        this.operator = operator;
        this.valueType = valueType;
        this.expectedValue = expectedValue;
    }

    protected RuleConditionEntity() {
    }

    public Long getId() {
        return id;
    }

    public ConditionGroupEntity getConditionGroup() {
        return conditionGroup;
    }

    public String getFactKey() {
        return factKey;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public String getExpectedValue() {
        return expectedValue;
    }
}
