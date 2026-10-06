package com.sdney.rulesengine.infrastructure.database.repository;

import com.sdney.rulesengine.domain.rule.RuleStatus;
import com.sdney.rulesengine.infrastructure.database.entity.RuleDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RuleDefinitionJpaRepository
        extends JpaRepository<RuleDefinitionEntity, Long> {

    Optional<RuleDefinitionEntity> findFirstByRuleKeyAndStatus(
            UUID ruleKey,
            RuleStatus status
    );
}
