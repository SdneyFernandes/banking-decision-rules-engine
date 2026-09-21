package com.sdney.rulesengine.infrastructure.database.repository;

import com.sdney.rulesengine.infrastructure.database.entity.ConditionGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConditionGroupJpaRepository
        extends JpaRepository<ConditionGroupEntity, Long> {
}
