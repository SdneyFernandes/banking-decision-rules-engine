package com.sdney.rulesengine.infrastructure.database.repository;

import com.sdney.rulesengine.infrastructure.database.entity.RuleDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleDefinitionJpaRepository
        extends JpaRepository<RuleDefinitionEntity, Long> {
}
