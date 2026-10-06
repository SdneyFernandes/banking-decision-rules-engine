package com.sdney.rulesengine.domain.rule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.UUID;

import static com.sdney.rulesengine.domain.rule.ComparisonOperator.GREATER_THAN;
import static com.sdney.rulesengine.domain.rule.LogicalOperator.AND;
import static com.sdney.rulesengine.domain.rule.RuleStatus.APPROVED;
import static com.sdney.rulesengine.domain.rule.RuleStatus.DRAFT;
import static com.sdney.rulesengine.domain.rule.RuleStatus.PUBLISHED;
import static com.sdney.rulesengine.domain.rule.RuleStatus.RETIRED;
import static com.sdney.rulesengine.domain.rule.ValueType.DECIMAL;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleDefinitionTest {

    @Test
    void shouldCreateRuleWithInitialState() {
        RuleDefinition rule =
                new RuleDefinition(
                        "RuleDefinition1",
                        10
                );

        assertAll(
                () -> assertEquals(
                        "RuleDefinition1",
                        rule.getName()
                ),
                () -> assertEquals(
                        10,
                        rule.getPriority()
                ),
                () -> assertEquals(
                        DRAFT,
                        rule.getStatus()
                ),
                () -> assertEquals(
                        1,
                        rule.getVersion()
                ),
                () -> assertNull(rule.getId()),
                () -> assertNotNull(rule.getRuleKey()),
                () -> assertTrue(
                        rule.getConditionGroups().isEmpty()
                )
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "  "})
    void shouldRejectInvalidName(String invalidName) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RuleDefinition(
                        invalidName,
                        10
                )
        );
    }

    @Test
    void shouldAcceptZeroPriority() {
        RuleDefinition rule =
                new RuleDefinition(
                        "RuleDefinition1",
                        0
                );

        assertEquals(0, rule.getPriority());
    }

    @Test
    void shouldRejectNegativePriority() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RuleDefinition(
                        "RuleDefinition1",
                        -1
                )
        );
    }

    @Test
    void shouldAddConditionGroupWhileDraft() {
        RuleDefinition rule =
                new RuleDefinition(
                        "RuleDefinition1",
                        10
                );
        ConditionGroup group =
                new ConditionGroup(AND);

        rule.addConditionGroup(group);

        assertTrue(
                rule.getConditionGroups().contains(group)
        );
    }

    @Test
    void shouldRejectNullConditionGroup() {
        RuleDefinition rule =
                new RuleDefinition(
                        "RuleDefinition1",
                        10
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> rule.addConditionGroup(null)
        );
    }

    @ParameterizedTest
    @EnumSource(
            value = RuleStatus.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = "DRAFT"
    )
    void shouldRejectAddingConditionGroupWhenRuleIsNotDraft(
            RuleStatus status
    ) {
        RuleDefinition rule = restoreWithStatus(status);

        assertThrows(
                IllegalStateException.class,
                () -> rule.addConditionGroup(
                        new ConditionGroup(AND)
                )
        );
    }

    @Test
    void shouldApproveConfiguredDraftRule() {
        RuleDefinition rule =
                new RuleDefinition(
                        "RuleDefinition1",
                        10
                );
        rule.addConditionGroup(validGroup());

        rule.approve();

        assertEquals(APPROVED, rule.getStatus());
    }

    @ParameterizedTest
    @EnumSource(
            value = RuleStatus.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = "DRAFT"
    )
    void shouldRejectApprovalWhenRuleIsNotDraft(
            RuleStatus status
    ) {
        RuleDefinition rule = restoreWithStatus(status);

        assertThrows(
                IllegalStateException.class,
                rule::approve
        );
    }

    @Test
    void shouldRejectApprovalWithoutConditionGroups() {
        RuleDefinition rule =
                new RuleDefinition(
                        "RuleDefinition1",
                        10
                );

        assertThrows(
                IllegalStateException.class,
                rule::approve
        );
    }

    @Test
    void shouldRejectApprovalWithEmptyConditionGroup() {
        RuleDefinition rule =
                new RuleDefinition(
                        "RuleDefinition1",
                        10
                );
        rule.addConditionGroup(
                new ConditionGroup(AND)
        );

        assertThrows(
                IllegalStateException.class,
                rule::approve
        );
    }

    @Test
    void shouldPublishApprovedRule() {
        RuleDefinition rule =
                RuleDefinition.restore(
                        1L,
                        "RuleDefinition1",
                        10,
                        APPROVED,
                        1,
                        List.of(validGroup()),
                        UUID.randomUUID()
                );

        rule.publish();

        assertEquals(PUBLISHED, rule.getStatus());
    }

    @ParameterizedTest
    @EnumSource(
            value = RuleStatus.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = "APPROVED"
    )
    void shouldRejectPublishWhenRuleIsNotApproved(
            RuleStatus status
    ) {
        RuleDefinition rule = restoreWithStatus(status);

        assertThrows(
                IllegalStateException.class,
                rule::publish
        );
    }

    @Test
    void shouldRetirePublishedRule() {
        RuleDefinition rule =
                RuleDefinition.restore(
                        1L,
                        "RuleDefinition1",
                        10,
                        PUBLISHED,
                        1,
                        List.of(validGroup()),
                        UUID.randomUUID()
                );

        rule.retire();

        assertEquals(RETIRED, rule.getStatus());
    }

    @ParameterizedTest
    @EnumSource(
            value = RuleStatus.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = "PUBLISHED"
    )
    void shouldRejectRetirementWhenRuleIsNotPublished(
            RuleStatus status
    ) {
        RuleDefinition rule = restoreWithStatus(status);

        assertThrows(
                IllegalStateException.class,
                rule::retire
        );
    }

    @Test
    void shouldCreateNewVersionFromPublishedRule() {
        UUID ruleKey = UUID.randomUUID();
        ConditionGroup originalGroup = validGroup();

        RuleDefinition original =
                RuleDefinition.restore(
                        10L,
                        "RuleDefinition1",
                        10,
                        PUBLISHED,
                        3,
                        List.of(originalGroup),
                        ruleKey
                );

        RuleDefinition newVersion =
                original.createNewVersion();

        ConditionGroup copiedGroup =
                newVersion.getConditionGroups().get(0);

        RuleCondition originalCondition =
                originalGroup.getConditions().get(0);

        RuleCondition copiedCondition =
                copiedGroup.getConditions().get(0);

        assertAll(
                () -> assertNull(newVersion.getId()),
                () -> assertEquals(
                        DRAFT,
                        newVersion.getStatus()
                ),
                () -> assertEquals(
                        4,
                        newVersion.getVersion()
                ),
                () -> assertEquals(
                        ruleKey,
                        newVersion.getRuleKey()
                ),
                () -> assertEquals(
                        original.getName(),
                        newVersion.getName()
                ),
                () -> assertEquals(
                        original.getPriority(),
                        newVersion.getPriority()
                ),
                () -> assertEquals(
                        1,
                        newVersion
                                .getConditionGroups()
                                .size()
                ),
                () -> assertNotSame(
                        originalGroup,
                        copiedGroup
                ),
                () -> assertEquals(
                        originalGroup.getLogicalOperator(),
                        copiedGroup.getLogicalOperator()
                ),
                () -> assertEquals(
                        originalCondition.getFactKey(),
                        copiedCondition.getFactKey()
                ),
                () -> assertEquals(
                        originalCondition.getOperator(),
                        copiedCondition.getOperator()
                ),
                () -> assertEquals(
                        originalCondition.getValueType(),
                        copiedCondition.getValueType()
                ),
                () -> assertEquals(
                        originalCondition.getExpectedValue(),
                        copiedCondition.getExpectedValue()
                ),
                () -> assertEquals(
                        PUBLISHED,
                        original.getStatus()
                ),
                () -> assertEquals(
                        3,
                        original.getVersion()
                )
        );
    }

    @ParameterizedTest
    @EnumSource(
            value = RuleStatus.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = "PUBLISHED"
    )
    void shouldRejectCreatingNewVersionWhenRuleIsNotPublished(
            RuleStatus status
    ) {
        RuleDefinition rule = restoreWithStatus(status);

        assertThrows(
                IllegalStateException.class,
                rule::createNewVersion
        );
    }

    @Test
    void shouldRestoreExistingRuleState() {
        UUID ruleKey = UUID.randomUUID();
        ConditionGroup group = validGroup();

        RuleDefinition rule =
                RuleDefinition.restore(
                        42L,
                        "ExistingRule",
                        7,
                        PUBLISHED,
                        5,
                        List.of(group),
                        ruleKey
                );

        assertAll(
                () -> assertEquals(42L, rule.getId()),
                () -> assertEquals(
                        "ExistingRule",
                        rule.getName()
                ),
                () -> assertEquals(
                        7,
                        rule.getPriority()
                ),
                () -> assertEquals(
                        PUBLISHED,
                        rule.getStatus()
                ),
                () -> assertEquals(
                        5,
                        rule.getVersion()
                ),
                () -> assertEquals(
                        ruleKey,
                        rule.getRuleKey()
                ),
                () -> assertEquals(
                        1,
                        rule.getConditionGroups().size()
                ),
                () -> assertFalse(
                        rule.getConditionGroups().isEmpty()
                )
        );
    }

    @Test
    void shouldExposeConditionGroupsAsImmutableList() {
        RuleDefinition rule =
                new RuleDefinition(
                        "RuleDefinition1",
                        10
                );
        rule.addConditionGroup(validGroup());

        assertThrows(
                UnsupportedOperationException.class,
                () -> rule.getConditionGroups()
                        .add(validGroup())
        );
    }

    private RuleDefinition restoreWithStatus(
            RuleStatus status
    ) {
        return RuleDefinition.restore(
                1L,
                "RuleDefinition1",
                10,
                status,
                1,
                List.of(),
                UUID.randomUUID()
        );
    }

    private ConditionGroup validGroup() {
        ConditionGroup group =
                new ConditionGroup(AND);

        group.addCondition(
                new RuleCondition(
                        "amount",
                        GREATER_THAN,
                        DECIMAL,
                        "10000"
                )
        );

        return group;
    }
}
