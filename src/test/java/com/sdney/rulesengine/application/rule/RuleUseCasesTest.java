package com.sdney.rulesengine.application.rule;

import com.sdney.rulesengine.domain.rule.ConditionGroup;
import com.sdney.rulesengine.domain.rule.RuleCondition;
import com.sdney.rulesengine.domain.rule.RuleDefinition;
import com.sdney.rulesengine.domain.rule.RuleStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sdney.rulesengine.domain.rule.ComparisonOperator.GREATER_THAN;
import static com.sdney.rulesengine.domain.rule.LogicalOperator.AND;
import static com.sdney.rulesengine.domain.rule.LogicalOperator.OR;
import static com.sdney.rulesengine.domain.rule.RuleStatus.APPROVED;
import static com.sdney.rulesengine.domain.rule.RuleStatus.DRAFT;
import static com.sdney.rulesengine.domain.rule.RuleStatus.PUBLISHED;
import static com.sdney.rulesengine.domain.rule.RuleStatus.RETIRED;
import static com.sdney.rulesengine.domain.rule.ValueType.DECIMAL;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuleUseCasesTest {

    @Mock
    private RuleRepository repository;

    @InjectMocks
    private RuleUseCases useCases;

    @Test
    void shouldCreateAndSaveRule() {
        String name = "High Value Transaction";
        int priority = 10;

        RuleDefinition savedRule =
                RuleDefinition.restore(
                        1L,
                        name,
                        priority,
                        DRAFT,
                        1,
                        List.of(),
                        UUID.randomUUID()
                );

        when(repository.save(any(RuleDefinition.class)))
                .thenReturn(savedRule);

        RuleDefinition result =
                useCases.create(name, priority);

        ArgumentCaptor<RuleDefinition> captor =
                ArgumentCaptor.forClass(
                        RuleDefinition.class
                );

        verify(repository).save(captor.capture());

        RuleDefinition capturedRule =
                captor.getValue();

        assertAll(
                () -> assertEquals(
                        name,
                        capturedRule.getName()
                ),
                () -> assertEquals(
                        priority,
                        capturedRule.getPriority()
                ),
                () -> assertSame(
                        savedRule,
                        result
                )
        );
    }

    @Test
    void shouldApproveRule() {
        Long id = 1L;
        RuleDefinition existingRule =
                RuleDefinition.restore(
                        id,
                        "RuleDefinition1",
                        10,
                        DRAFT,
                        1,
                        List.of(validGroup(AND)),
                        UUID.randomUUID()
                );

        when(repository.findById(id))
                .thenReturn(Optional.of(existingRule));

        when(repository.save(existingRule))
                .thenReturn(existingRule);

        RuleDefinition result =
                useCases.approve(id);

        assertEquals(
                APPROVED,
                existingRule.getStatus()
        );
        assertSame(existingRule, result);

        verify(repository).findById(id);
        verify(repository).save(existingRule);
    }

    @Test
    void shouldNotApproveWhenRuleNotFound() {
        Long id = 999L;

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> useCases.approve(id)
        );

        verify(repository).findById(id);
        verify(repository, never())
                .save(any(RuleDefinition.class));
    }

    @Test
    void shouldPublishRuleWhenThereIsNoPreviousPublishedVersion() {
        UUID ruleKey = UUID.randomUUID();

        RuleDefinition approvedRule =
                RuleDefinition.restore(
                        1L,
                        "RuleDefinition1",
                        10,
                        APPROVED,
                        1,
                        List.of(validGroup(AND)),
                        ruleKey
                );

        when(repository.findById(approvedRule.getId()))
                .thenReturn(Optional.of(approvedRule));

        when(repository.findPublishedByRuleKey(ruleKey))
                .thenReturn(Optional.empty());

        when(repository.save(approvedRule))
                .thenReturn(approvedRule);

        RuleDefinition result =
                useCases.publish(approvedRule.getId());

        assertEquals(
                PUBLISHED,
                approvedRule.getStatus()
        );
        assertSame(approvedRule, result);

        verify(repository)
                .findById(approvedRule.getId());
        verify(repository)
                .findPublishedByRuleKey(ruleKey);
        verify(repository)
                .save(approvedRule);
    }

    @Test
    void shouldRetirePreviousPublishedRuleWhenPublishingNewVersion() {
        UUID ruleKey = UUID.randomUUID();

        RuleDefinition oldPublishedRule =
                RuleDefinition.restore(
                        1L,
                        "RuleDefinition1",
                        10,
                        PUBLISHED,
                        1,
                        List.of(validGroup(AND)),
                        ruleKey
                );

        RuleDefinition newApprovedRule =
                RuleDefinition.restore(
                        2L,
                        "RuleDefinition1",
                        10,
                        APPROVED,
                        2,
                        List.of(validGroup(AND)),
                        ruleKey
                );

        when(repository.findById(newApprovedRule.getId()))
                .thenReturn(Optional.of(newApprovedRule));

        when(repository.findPublishedByRuleKey(ruleKey))
                .thenReturn(
                        Optional.of(oldPublishedRule)
                );

        when(repository.save(oldPublishedRule))
                .thenReturn(oldPublishedRule);

        when(repository.save(newApprovedRule))
                .thenReturn(newApprovedRule);

        RuleDefinition result =
                useCases.publish(
                        newApprovedRule.getId()
                );

        assertAll(
                () -> assertEquals(
                        RETIRED,
                        oldPublishedRule.getStatus()
                ),
                () -> assertEquals(
                        PUBLISHED,
                        newApprovedRule.getStatus()
                ),
                () -> assertSame(
                        newApprovedRule,
                        result
                )
        );

        verify(repository)
                .findById(newApprovedRule.getId());
        verify(repository)
                .findPublishedByRuleKey(ruleKey);
        verify(repository)
                .save(oldPublishedRule);
        verify(repository)
                .save(newApprovedRule);
    }

    @Test
    void shouldKeepCurrentRulePublishedWhenRepositoryReturnsSameRule() {
        UUID ruleKey = UUID.randomUUID();

        RuleDefinition approvedRule =
                RuleDefinition.restore(
                        1L,
                        "RuleDefinition1",
                        10,
                        APPROVED,
                        1,
                        List.of(validGroup(AND)),
                        ruleKey
                );

        when(repository.findById(approvedRule.getId()))
                .thenReturn(Optional.of(approvedRule));

        when(repository.findPublishedByRuleKey(ruleKey))
                .thenReturn(
                        Optional.of(approvedRule)
                );

        when(repository.save(approvedRule))
                .thenReturn(approvedRule);

        RuleDefinition result =
                useCases.publish(approvedRule.getId());

        assertEquals(
                PUBLISHED,
                approvedRule.getStatus()
        );
        assertSame(approvedRule, result);

        verify(repository, times(1))
                .save(approvedRule);
    }

    @Test
    void shouldNotPublishWhenRuleNotFound() {
        Long id = 999L;

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> useCases.publish(id)
        );

        verify(repository).findById(id);
        verify(repository, never())
                .findPublishedByRuleKey(any(UUID.class));
        verify(repository, never())
                .save(any(RuleDefinition.class));
    }

    @Test
    void shouldAddConditionGroupToRule() {
        Long ruleId = 1L;

        RuleDefinition rule =
                RuleDefinition.restore(
                        ruleId,
                        "RuleDefinition1",
                        10,
                        DRAFT,
                        1,
                        List.of(),
                        UUID.randomUUID()
                );

        ConditionGroup group =
                new ConditionGroup(AND);

        when(repository.findById(ruleId))
                .thenReturn(Optional.of(rule));

        RuleDefinition result =
                useCases.addConditionGroup(
                        ruleId,
                        group
                );

        assertTrue(
                rule.getConditionGroups()
                        .contains(group)
        );
        assertSame(rule, result);

        verify(repository).findById(ruleId);
        verify(repository)
                .addConditionGroup(ruleId, group);
    }

    @Test
    void shouldNotAddConditionGroupWhenRuleNotFound() {
        Long ruleId = 999L;
        ConditionGroup group =
                new ConditionGroup(AND);

        when(repository.findById(ruleId))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> useCases.addConditionGroup(
                        ruleId,
                        group
                )
        );

        verify(repository).findById(ruleId);
        verify(repository, never())
                .addConditionGroup(
                        anyLong(),
                        any(ConditionGroup.class)
                );
    }

    @Test
    void shouldCreateAndPersistNewRuleVersion() {
        Long currentRuleId = 1L;
        UUID ruleKey = UUID.randomUUID();

        RuleDefinition currentRule =
                RuleDefinition.restore(
                        currentRuleId,
                        "RuleDefinition1",
                        10,
                        PUBLISHED,
                        1,
                        List.of(
                                validGroup(AND),
                                validGroup(OR)
                        ),
                        ruleKey
                );

        RuleDefinition savedVersion =
                RuleDefinition.restore(
                        2L,
                        "RuleDefinition1",
                        10,
                        DRAFT,
                        2,
                        List.of(),
                        ruleKey
                );

        RuleDefinition reloadedVersion =
                RuleDefinition.restore(
                        2L,
                        "RuleDefinition1",
                        10,
                        DRAFT,
                        2,
                        List.of(
                                validGroup(AND),
                                validGroup(OR)
                        ),
                        ruleKey
                );

        when(repository.findById(currentRuleId))
                .thenReturn(Optional.of(currentRule));

        when(repository.save(any(RuleDefinition.class)))
                .thenReturn(savedVersion);

        when(repository.findById(savedVersion.getId()))
                .thenReturn(
                        Optional.of(reloadedVersion)
                );

        RuleDefinition result =
                useCases.createNewVersion(
                        currentRuleId
                );

        ArgumentCaptor<RuleDefinition> captor =
                ArgumentCaptor.forClass(
                        RuleDefinition.class
                );

        verify(repository).save(captor.capture());

        RuleDefinition newVersion =
                captor.getValue();

        assertAll(
                () -> assertNull(newVersion.getId()),
                () -> assertEquals(
                        DRAFT,
                        newVersion.getStatus()
                ),
                () -> assertEquals(
                        2,
                        newVersion.getVersion()
                ),
                () -> assertEquals(
                        ruleKey,
                        newVersion.getRuleKey()
                ),
                () -> assertEquals(
                        2,
                        newVersion
                                .getConditionGroups()
                                .size()
                ),
                () -> assertSame(
                        reloadedVersion,
                        result
                )
        );

        verify(repository)
                .findById(currentRuleId);

        verify(repository, times(2))
                .addConditionGroup(
                        eq(savedVersion.getId()),
                        any(ConditionGroup.class)
                );

        verify(repository)
                .findById(savedVersion.getId());
    }

    @Test
    void shouldNotCreateNewVersionWhenRuleNotFound() {
        Long id = 999L;

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> useCases.createNewVersion(id)
        );

        verify(repository).findById(id);
        verify(repository, never())
                .save(any(RuleDefinition.class));
        verify(repository, never())
                .addConditionGroup(
                        anyLong(),
                        any(ConditionGroup.class)
                );
    }

    @Test
    void shouldThrowWhenNewVersionCannotBeReloaded() {
        Long currentRuleId = 1L;
        UUID ruleKey = UUID.randomUUID();

        RuleDefinition currentRule =
                RuleDefinition.restore(
                        currentRuleId,
                        "RuleDefinition1",
                        10,
                        PUBLISHED,
                        1,
                        List.of(validGroup(AND)),
                        ruleKey
                );

        RuleDefinition savedVersion =
                RuleDefinition.restore(
                        2L,
                        "RuleDefinition1",
                        10,
                        DRAFT,
                        2,
                        List.of(),
                        ruleKey
                );

        when(repository.findById(currentRuleId))
                .thenReturn(Optional.of(currentRule));

        when(repository.save(any(RuleDefinition.class)))
                .thenReturn(savedVersion);

        when(repository.findById(savedVersion.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalStateException.class,
                () -> useCases.createNewVersion(
                        currentRuleId
                )
        );

        verify(repository)
                .findById(currentRuleId);
        verify(repository)
                .save(any(RuleDefinition.class));
        verify(repository)
                .addConditionGroup(
                        eq(savedVersion.getId()),
                        any(ConditionGroup.class)
                );
        verify(repository)
                .findById(savedVersion.getId());
    }

    private ConditionGroup validGroup(
            com.sdney.rulesengine.domain.rule.LogicalOperator operator
    ) {
        ConditionGroup group =
                new ConditionGroup(operator);

        RuleCondition condition =
                new RuleCondition(
                        "amount",
                        GREATER_THAN,
                        DECIMAL,
                        "10000"
                );

        group.addCondition(condition);
        return group;
    }
}
