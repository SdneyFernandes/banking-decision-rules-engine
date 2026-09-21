package com.sdney.rulesengine.infrastructure.database.repository;

import com.sdney.rulesengine.infrastructure.database.entity.RuleConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleConditionJpaRepository
        extends JpaRepository<RuleConditionEntity, Long> {
}