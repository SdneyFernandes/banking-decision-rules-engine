package com.sdney.rulesengine.infrastructure.database.entity;

import com.sdney.rulesengine.domain.rule.RuleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.Generated;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Column(
            name = "version",
            nullable = false
    )
    private Integer version;

    @Column(
            name = "rule_key",
            nullable = false,
            updatable = false
    )
    private UUID ruleKey;


    public RuleDefinitionEntity(String name, RuleStatus status, Integer priority, Integer version, UUID ruleKey) {
        this.name = name;
        this.status = status;
        this.priority = priority;
        this.version = version;
        this.ruleKey = ruleKey;
    }

    public void updateStatus(RuleStatus status) {
        this.status = status;
    }

    protected RuleDefinitionEntity() {
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

    public int getPriority() {
        return priority;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ConditionGroupEntity> getConditionGroups() {
        return conditionGroups;
    }

    public int getVersion() {
        return version;
    }

    public UUID getRuleKey() {
        return ruleKey;
    }
}
