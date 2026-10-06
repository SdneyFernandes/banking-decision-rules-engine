package com.sdney.rulesengine.application.rule;

import com.sdney.rulesengine.domain.rule.ConditionGroup;
import com.sdney.rulesengine.domain.rule.RuleDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RuleUseCases {

    private final RuleRepository repository;

    public RuleUseCases(RuleRepository repository) {
        this.repository = repository;
    }

    public RuleDefinition create(String name, int priority) {
        RuleDefinition rule = new RuleDefinition(name, priority);
        return repository.save(rule);
    }

    public RuleDefinition approve(Long id) {
        RuleDefinition rule = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Rule not found")
                );

        rule.approve();
        return repository.save(rule);
    }

    @Transactional
    public RuleDefinition publish(Long id) {
        RuleDefinition rule = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Rule not found")
                );

        rule.publish();

        Optional<RuleDefinition> currentPublished =
                repository.findPublishedByRuleKey(
                        rule.getRuleKey()
                );

        currentPublished
                .filter(current -> !current.getId().equals(rule.getId()))
                .ifPresent(current -> {
                    current.retire();
                    repository.save(current);
                });

        return repository.save(rule);
    }

    public RuleDefinition addConditionGroup(
            Long ruleId,
            ConditionGroup group
    ) {
        RuleDefinition rule = repository.findById(ruleId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Rule not found")
                );

        rule.addConditionGroup(group);
        repository.addConditionGroup(ruleId, group);
        return rule;
    }

    @Transactional
    public RuleDefinition createNewVersion(Long id) {
        RuleDefinition currentRule = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Rule not found")
                );

        RuleDefinition newVersion =
                currentRule.createNewVersion();

        RuleDefinition savedVersion =
                repository.save(newVersion);

        for (ConditionGroup group : newVersion.getConditionGroups()) {
            repository.addConditionGroup(
                    savedVersion.getId(),
                    group
            );
        }

        return repository.findById(savedVersion.getId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "New rule version could not be loaded"
                        )
                );
    }
}
