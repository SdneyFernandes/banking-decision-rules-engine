package com.sdney.rulesengine.infrastructure.database.entity;


import com.sdney.rulesengine.domain.rule.RuleStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.Generated;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rule_definition")
public class RuleDefinitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "name",
            nullable = false,
            length = 120
    )
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private RuleStatus status;

    @Column(
            name = "priority",
            nullable = false
    )
    private Integer priority;

    @Generated
    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;

    @OneToMany(
            mappedBy = "ruleDefinition",
            fetch = FetchType.LAZY
    )
    private List<ConditionGroupEntity> conditionGroups = new ArrayList<>();

    public RuleDefinitionEntity(String name, RuleStatus status, Integer priority, List<ConditionGroupEntity> conditionGroups) {
        this.name = name;
        this.status = status;
        this.priority = priority;
        this.conditionGroups = conditionGroups;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RuleStatus getStatus() {
        return status;
    }

    public Integer getPriority() {
        return priority;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ConditionGroupEntity> getConditionGroups() {
        return conditionGroups;
    }

    protected RuleDefinitionEntity() {}
}
