package com.sdney.rulesengine.infrastructure.database.adapter;

import com.sdney.rulesengine.application.rule.RuleRepository;
import com.sdney.rulesengine.domain.rule.ConditionGroup;
import com.sdney.rulesengine.domain.rule.RuleCondition;
import com.sdney.rulesengine.domain.rule.RuleDefinition;
import com.sdney.rulesengine.domain.rule.RuleStatus;
import com.sdney.rulesengine.infrastructure.database.entity.ConditionGroupEntity;
import com.sdney.rulesengine.infrastructure.database.entity.RuleConditionEntity;
import com.sdney.rulesengine.infrastructure.database.entity.RuleDefinitionEntity;
import com.sdney.rulesengine.infrastructure.database.repository.ConditionGroupJpaRepository;
import com.sdney.rulesengine.infrastructure.database.repository.RuleConditionJpaRepository;
import com.sdney.rulesengine.infrastructure.database.repository.RuleDefinitionJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRuleRepositoryAdapter implements RuleRepository {

    private final RuleDefinitionJpaRepository ruleDefinitionRepository;
    private final ConditionGroupJpaRepository conditionGroupRepository;
    private final RuleConditionJpaRepository ruleConditionRepository;

    public JpaRuleRepositoryAdapter(
            RuleDefinitionJpaRepository ruleDefinitionRepository,
            ConditionGroupJpaRepository conditionGroupRepository,
            RuleConditionJpaRepository ruleConditionRepository
    ) {
        this.ruleDefinitionRepository = ruleDefinitionRepository;
        this.conditionGroupRepository = conditionGroupRepository;
        this.ruleConditionRepository = ruleConditionRepository;
    }

    @Override
    @Transactional
    public RuleDefinition save(RuleDefinition rule) {
        if (rule.getId() == null) {
            RuleDefinitionEntity entity =
                    new RuleDefinitionEntity(
                            rule.getName(),
                            rule.getStatus(),
                            rule.getPriority(),
                            rule.getVersion(),
                            rule.getRuleKey()
                    );

            RuleDefinitionEntity saved =
                    ruleDefinitionRepository.save(entity);

            return toDomain(saved);
        }

        RuleDefinitionEntity entity =
                ruleDefinitionRepository.findById(rule.getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Rule not found"
                                )
                        );

        entity.updateStatus(rule.getStatus());

        RuleDefinitionEntity saved =
                ruleDefinitionRepository.save(entity);

        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RuleDefinition> findById(Long id) {
        return ruleDefinitionRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public void addConditionGroup(
            Long ruleId,
            ConditionGroup group
    ) {
        RuleDefinitionEntity ruleEntity =
                ruleDefinitionRepository.findById(ruleId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Rule not found"
                                )
                        );

        ConditionGroupEntity groupEntity =
                new ConditionGroupEntity(
                        ruleEntity,
                        group.getLogicalOperator()
                );

        ConditionGroupEntity savedGroup =
                conditionGroupRepository.save(groupEntity);

        for (RuleCondition condition : group.getConditions()) {
            RuleConditionEntity conditionEntity =
                    new RuleConditionEntity(
                            savedGroup,
                            condition.getFactKey(),
                            condition.getOperator(),
                            condition.getValueType(),
                            condition.getExpectedValue()
                    );

            ruleConditionRepository.save(conditionEntity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RuleDefinition> findPublishedByRuleKey(
            UUID ruleKey
    ) {
        return ruleDefinitionRepository
                .findFirstByRuleKeyAndStatus(
                        ruleKey,
                        RuleStatus.PUBLISHED
                )
                .map(this::toDomain);
    }

    private RuleDefinition toDomain(
            RuleDefinitionEntity entity
    ) {
        List<ConditionGroup> groups =
                entity.getConditionGroups().stream()
                        .map(this::toDomain)
                        .toList();

        return RuleDefinition.restore(
                entity.getId(),
                entity.getName(),
                entity.getPriority(),
                entity.getStatus(),
                entity.getVersion(),
                groups,
                entity.getRuleKey()
        );
    }

    private ConditionGroup toDomain(
            ConditionGroupEntity entity
    ) {
        ConditionGroup group =
                new ConditionGroup(
                        entity.getLogicalOperator()
                );

        entity.getConditions().stream()
                .map(this::toDomain)
                .forEach(group::addCondition);

        return group;
    }

    private RuleCondition toDomain(
            RuleConditionEntity entity
    ) {
        return new RuleCondition(
                entity.getFactKey(),
                entity.getOperator(),
                entity.getValueType(),
                entity.getExpectedValue()
        );
    }
}
