package com.sdney.rulesengine.application.rule;

import com.sdney.rulesengine.domain.rule.ConditionGroup;
import com.sdney.rulesengine.domain.rule.RuleDefinition;

import java.util.Optional;
import java.util.UUID;

public interface RuleRepository {

    RuleDefinition save(RuleDefinition rule);

    Optional<RuleDefinition> findById(Long id);

    void addConditionGroup(Long ruleId, ConditionGroup group);

    Optional<RuleDefinition> findPublishedByRuleKey(UUID ruleKey);
}
