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

    private final RuleDefinitionJpaRepository ruleJpaRepository;
    private final ConditionGroupJpaRepository groupJpaRepository;
    private final RuleConditionJpaRepository conditionJpaRepository;

    public JpaRuleRepositoryAdapter(RuleDefinitionJpaRepository jpaRepository,
                                    ConditionGroupJpaRepository groupJpaRepository,
                                    RuleConditionJpaRepository conditionJpaRepository) {
        this.ruleJpaRepository = jpaRepository;
        this.groupJpaRepository = groupJpaRepository;
        this.conditionJpaRepository = conditionJpaRepository;
    }

    @Override
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

            RuleDefinitionEntity savedEntity =
                    ruleJpaRepository.save(entity);

            return RuleDefinition.restore(
                    savedEntity.getId(),
                    savedEntity.getName(),
                    savedEntity.getPriority(),
                    savedEntity.getStatus(),
                    savedEntity.getVersion(),
                    List.of(),
                    savedEntity.getRuleKey()
            );
        }

        RuleDefinitionEntity entity =
                ruleJpaRepository.findById(rule.getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException("Rule not found")
                        );

        entity.updateStatus(rule.getStatus());

        ruleJpaRepository.save(entity);

        return rule;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<RuleDefinition> findById(Long id) {

        return ruleJpaRepository.findById(id)
                .map(entity -> {

                    List<ConditionGroup> groups =
                            entity.getConditionGroups()
                                    .stream()
                                    .map(groupEntity -> {

                                        ConditionGroup group =
                                                new ConditionGroup(
                                                        groupEntity.getLogicalOperator()
                                                );

                                        groupEntity.getConditions()
                                                .forEach(conditionEntity -> {

                                                    RuleCondition condition =
                                                            new RuleCondition(
                                                                    conditionEntity.getFactKey(),
                                                                    conditionEntity.getOperator(),
                                                                    conditionEntity.getValueType(),
                                                                    conditionEntity.getExpectedValue()
                                                            );

                                                    group.addCondition(condition);
                                                });

                                        return group;
                                    })
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
                });
    }


    @Transactional
    @Override
    public void addConditionGroup(
            Long ruleId,
            ConditionGroup group
    ) {

        RuleDefinitionEntity ruleEntity =
                ruleJpaRepository.findById(ruleId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Rule not found")
                        );

        ConditionGroupEntity groupEntity =
                new ConditionGroupEntity(
                        ruleEntity,
                        group.getLogicalOperator()
                );

        ConditionGroupEntity savedGroup =
                groupJpaRepository.save(groupEntity);

        for (RuleCondition condition : group.getConditions()) {

            RuleConditionEntity conditionEntity =
                    new RuleConditionEntity(
                            savedGroup,
                            condition.getFactKey(),
                            condition.getOperator(),
                            condition.getValueType(),
                            condition.getExpectedValue()
                    );

            conditionJpaRepository.save(conditionEntity);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<RuleDefinition> findPublishedByRuleKey(UUID ruleKey) {

        return ruleJpaRepository
                .findFirstByRuleKeyAndStatus(
                        ruleKey,
                        RuleStatus.PUBLISHED
                )
                .map(entity -> {

                    List<ConditionGroup> groups =
                            entity.getConditionGroups()
                                    .stream()
                                    .map(groupEntity -> {

                                        ConditionGroup group =
                                                new ConditionGroup(
                                                        groupEntity.getLogicalOperator()
                                                );

                                        groupEntity.getConditions()
                                                .forEach(conditionEntity -> {

                                                    RuleCondition condition =
                                                            new RuleCondition(
                                                                    conditionEntity.getFactKey(),
                                                                    conditionEntity.getOperator(),
                                                                    conditionEntity.getValueType(),
                                                                    conditionEntity.getExpectedValue()
                                                            );

                                                    group.addCondition(condition);
                                                });

                                        return group;
                                    })
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
                });
    }


}
