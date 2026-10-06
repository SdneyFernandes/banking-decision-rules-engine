package com.sdney.rulesengine.application.rule;

import com.sdney.rulesengine.domain.rule.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.sdney.rulesengine.domain.rule.ComparisonOperator.GREATER_THAN;
import static com.sdney.rulesengine.domain.rule.RuleStatus.*;
import static com.sdney.rulesengine.domain.rule.ValueType.DECIMAL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        RuleDefinition savedRule = RuleDefinition.restore(
                        1L,
                        name,
                        priority,
                        DRAFT,
                        1,
                        List.of(),
                        UUID.randomUUID()
                );

        when(repository.save(any(RuleDefinition.class))).thenReturn(savedRule);
        RuleDefinition result = useCases.create(name, priority);
        ArgumentCaptor<RuleDefinition> captor = ArgumentCaptor.forClass(RuleDefinition.class);
        verify(repository).save(captor.capture());
        RuleDefinition capturedRule = captor.getValue();

        assertAll(
                () -> assertEquals(name, capturedRule.getName()),
                () -> assertEquals(priority, capturedRule.getPriority()),
                () -> assertSame(savedRule, result)
        );
    }

    @Test
    void shouldApproveRule() {

        Long id = 1L;

        ConditionGroup conditionGroup = new ConditionGroup(LogicalOperator.AND);
        RuleCondition condition = new RuleCondition(
                "amount",
                GREATER_THAN,
                DECIMAL,
                "10000"
        );
        conditionGroup.addCondition(condition);
        List<ConditionGroup> conditionGroups = List.of(conditionGroup);

        RuleDefinition savedRule = RuleDefinition.restore(
                1L,
                "RuleDefintition",
                10,
                DRAFT,
                1,
                conditionGroups,
                UUID.randomUUID()

        );

        when(repository.findById(id)).thenReturn(Optional.of(savedRule));
        when(repository.save(any(RuleDefinition.class))).thenReturn(savedRule);

        savedRule = useCases.approve(id);

        verify(repository).findById(id);
        verify(repository).save(any(RuleDefinition.class));

        ArgumentCaptor<RuleDefinition> captor = ArgumentCaptor.forClass(RuleDefinition.class);
        verify(repository).save(captor.capture());
        RuleDefinition capturedRule = captor.getValue();

        assertAll(
                ()-> assertEquals("RuleDefintition", capturedRule.getName()),
                () -> assertEquals(APPROVED, capturedRule.getStatus())
        );

    }

    @Test
    void shouldRetirePreviousPublishedRuleWhenPublishingNewVersion() {

        UUID ruleKey = UUID.randomUUID();

        RuleCondition condition = new RuleCondition(
                "amount",
                GREATER_THAN,
                DECIMAL,
                "10000"
        );

        ConditionGroup group = new ConditionGroup(LogicalOperator.AND);
        group.addCondition(condition);

        List<ConditionGroup> conditionGroups = List.of(group);

        RuleDefinition oldPublishedRule = RuleDefinition.restore(
                1L,
                "RuleDefinition1",
                10,
                PUBLISHED,
                1,
                conditionGroups,
                ruleKey
        );

        RuleDefinition newApprovedRule = RuleDefinition.restore(
                2L,
                "RuleDefinition1",
                10,
                APPROVED,
                2,
                conditionGroups,
                ruleKey
        );

        when(repository.findById(newApprovedRule.getId()))
                .thenReturn(Optional.of(newApprovedRule));

        when(repository.findPublishedByRuleKey(ruleKey))
                .thenReturn(Optional.of(oldPublishedRule));

        when(repository.save(oldPublishedRule))
                .thenReturn(oldPublishedRule);

        when(repository.save(newApprovedRule))
                .thenReturn(newApprovedRule);

        RuleDefinition result =
                useCases.publish(newApprovedRule.getId());

        assertAll(
                () -> assertEquals(RETIRED, oldPublishedRule.getStatus()),
                () -> assertEquals(PUBLISHED, newApprovedRule.getStatus()),
                () -> assertSame(newApprovedRule, result)
        );

        verify(repository).findById(newApprovedRule.getId());
        verify(repository).findPublishedByRuleKey(ruleKey);

        verify(repository).save(oldPublishedRule);
        verify(repository).save(newApprovedRule);
    }

    @Test
    void shouldRetirePreviousPublishedVersionWhenPublishingNewVersion() {

        UUID ruleKey = UUID.randomUUID();

        RuleDefinition oldRule = RuleDefinition.restore(
                1L,
                "RuleDefinition1",
                10,
                PUBLISHED,
                1,
                List.of(),
                ruleKey
        );

        RuleDefinition newRule = RuleDefinition.restore(
                2L,
                "RuleDefinition1",
                10,
                APPROVED,
                2,
                List.of(),
                ruleKey
        );

        when(repository.findById(newRule.getId()))
                .thenReturn(Optional.of(newRule));

        when(repository.findPublishedByRuleKey(ruleKey))
                .thenReturn(Optional.of(oldRule));

        when(repository.save(oldRule))
                .thenReturn(oldRule);

        when(repository.save(newRule))
                .thenReturn(newRule);

        RuleDefinition result =
                useCases.publish(newRule.getId());

        assertAll(
                () -> assertEquals(RETIRED, oldRule.getStatus()),
                () -> assertEquals(PUBLISHED, newRule.getStatus()),
                () -> assertSame(newRule, result)
        );

        verify(repository).findById(newRule.getId());
        verify(repository).findPublishedByRuleKey(ruleKey);

        verify(repository).save(oldRule);
        verify(repository).save(newRule);
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
    void shouldNotApproveWhenRuleNotFound() {

        Long id = 999L;

        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(
                IllegalArgumentException.class,
                () -> useCases.approve(id)
        );

        verify(repository).findById(id);
        verify(repository, never()).save(any(RuleDefinition.class));
    }


    @Test
    void shouldAddConditionGroupToRule() {

        Long ruleId = 1L;

        RuleDefinition rule = RuleDefinition.restore(
                ruleId,
                "RuleDefinition1",
                10,
                DRAFT,
                1,
                List.of(),
                UUID.randomUUID()
        );

        ConditionGroup group =
                new ConditionGroup(LogicalOperator.AND);

        when(repository.findById(ruleId))
                .thenReturn(Optional.of(rule));

        RuleDefinition result =
                useCases.addConditionGroup(ruleId, group);

        assertTrue(
                rule.getConditionGroups().contains(group)
        );

        assertSame(rule, result);

        verify(repository).findById(ruleId);
        verify(repository).addConditionGroup(ruleId, group);
    }

    @Test
    void shouldNotAddConditionGroupWhenRuleNotFound() {

        Long ruleId = 999L;

        ConditionGroup group =
                new ConditionGroup(LogicalOperator.AND);

        when(repository.findById(ruleId))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> useCases.addConditionGroup(ruleId, group)
        );

        verify(repository).findById(ruleId);

        verify(repository, never())
                .addConditionGroup(
                        anyLong(),
                        any(ConditionGroup.class)
                );
    }



}