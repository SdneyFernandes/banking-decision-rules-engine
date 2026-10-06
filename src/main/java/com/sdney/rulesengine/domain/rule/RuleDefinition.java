package com.sdney.rulesengine.domain.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RuleDefinition {

    private Long id;
    private final String name;
    private final int priority;
    private RuleStatus status;
    private int version;
    private final UUID ruleKey;
    private final List<ConditionGroup> conditionGroups = new ArrayList<>();

    public RuleDefinition(String name, int priority) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Rule name is required");
        }
        if (priority < 0) {
            throw new IllegalArgumentException("Rule priority cannot be negative");
        }

        this.name = name;
        this.priority = priority;
        this.status = RuleStatus.DRAFT;
        this.version = 1;
        this.ruleKey = UUID.randomUUID();
    }

    private RuleDefinition(
            Long id,
            String name,
            int priority,
            RuleStatus status,
            int version,
            List<ConditionGroup> conditionGroups,
            UUID ruleKey
    ) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.status = status;
        this.version = version;
        this.ruleKey = ruleKey;
        this.conditionGroups.addAll(conditionGroups);
    }

    private RuleDefinition(
            String name,
            int priority,
            int version,
            UUID ruleKey,
            List<ConditionGroup> conditionGroups
    ) {
        this.id = null;
        this.name = name;
        this.priority = priority;
        this.status = RuleStatus.DRAFT;
        this.version = version;
        this.ruleKey = ruleKey;
        this.conditionGroups.addAll(conditionGroups);
    }

    public static RuleDefinition restore(
            Long id,
            String name,
            int priority,
            RuleStatus status,
            int version,
            List<ConditionGroup> conditionGroups,
            UUID ruleKey
    ) {
        return new RuleDefinition(
                id,
                name,
                priority,
                status,
                version,
                conditionGroups,
                ruleKey
        );
    }

    public void addConditionGroup(ConditionGroup group) {
        if (status != RuleStatus.DRAFT) {
            throw new IllegalStateException(
                    "Condition groups can only be added while rule is DRAFT"
            );
        }
        if (group == null) {
            throw new IllegalArgumentException("Condition group is required");
        }
        conditionGroups.add(group);
    }

    public void approve() {
        if (status != RuleStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT rules can be approved");
        }
        if (conditionGroups.isEmpty()) {
            throw new IllegalStateException(
                    "Rule must have at least one condition group"
            );
        }
        for (ConditionGroup group : conditionGroups) {
            if (group.getConditions().isEmpty()) {
                throw new IllegalStateException(
                        "Condition group must have at least one condition"
                );
            }
        }
        this.status = RuleStatus.APPROVED;
    }

    public void publish() {
        if (status != RuleStatus.APPROVED) {
            throw new IllegalStateException(
                    "Only APPROVED rules can be published"
            );
        }
        this.status = RuleStatus.PUBLISHED;
    }

    public void retire() {
        if (status != RuleStatus.PUBLISHED) {
            throw new IllegalStateException(
                    "Only PUBLISHED rules can be retired"
            );
        }
        this.status = RuleStatus.RETIRED;
    }

    public RuleDefinition createNewVersion() {
        if (status != RuleStatus.PUBLISHED) {
            throw new IllegalStateException(
                    "Only PUBLISHED rules can create a new version"
            );
        }

        List<ConditionGroup> copiedGroups =
                conditionGroups.stream()
                        .map(group -> {
                            ConditionGroup copiedGroup =
                                    new ConditionGroup(
                                            group.getLogicalOperator()
                                    );

                            group.getConditions()
                                    .forEach(copiedGroup::addCondition);

                            return copiedGroup;
                        })
                        .toList();

        return new RuleDefinition(
                name,
                priority,
                version + 1,
                ruleKey,
                copiedGroups
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public RuleStatus getStatus() {
        return status;
    }

    public int getVersion() {
        return version;
    }

    public UUID getRuleKey() {
        return ruleKey;
    }

    public List<ConditionGroup> getConditionGroups() {
        return List.copyOf(conditionGroups);
    }
}
