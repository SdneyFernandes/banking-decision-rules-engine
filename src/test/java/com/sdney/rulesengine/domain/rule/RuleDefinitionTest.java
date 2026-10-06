package com.sdney.rulesengine.domain.rule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.sdney.rulesengine.domain.rule.ComparisonOperator.GREATER_THAN;
import static com.sdney.rulesengine.domain.rule.RuleStatus.*;
import static com.sdney.rulesengine.domain.rule.ValueType.DECIMAL;
import static org.junit.jupiter.api.Assertions.*;

class RuleDefinitionTest {

    @Test
    void shouldCreateRuleWithInitialState(){

        String name = "ruleDefintion1";
        int priority = 10;

        RuleDefinition ruleDefinition = new RuleDefinition(name, priority);

        assertAll(
                () -> assertEquals(name, ruleDefinition.getName()),
                () -> assertEquals(priority, ruleDefinition.getPriority()),
                () -> assertEquals(1, ruleDefinition.getVersion()),
                () -> assertEquals(DRAFT, ruleDefinition.getStatus())

        );

        assertNotNull(ruleDefinition.getRuleKey());
        assertTrue(ruleDefinition.getConditionGroups().isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "  "})
    void shouldRejectInvalidName(String invalidName) {

        int priority = 10;

        assertThrows(IllegalArgumentException.class, () -> new RuleDefinition(invalidName, priority));

    }

    @ParameterizedTest
    @ValueSource(ints = {0,1})
    void shouldAcceptValidPriority(int validPriority) {

        String name = "ruleDefintion1";

        RuleDefinition ruleDefinition = new RuleDefinition(name, validPriority);
        assertEquals(validPriority, ruleDefinition.getPriority());
    }

    @Test
    void shouldRejectInvalidPriority() {

        String name = "ruleDefintion1";
        int priority = -1;
        assertThrows(IllegalArgumentException.class, () -> new RuleDefinition(name, priority));
    }

    @Test
    void shouldAddConditionGroup() {
        String name = "ruleDefintion1";
        int priority = 10;
        LogicalOperator logicalOperator = LogicalOperator.AND;

        ConditionGroup conditionGroup = new ConditionGroup(logicalOperator);
        RuleDefinition ruleDefinition = new RuleDefinition(name, priority);
        ruleDefinition.addConditionGroup(conditionGroup);
        assertTrue(
                ruleDefinition.getConditionGroups()
                        .contains(conditionGroup)
        );
    }

    @Test
    void shouldRejectAddingConditionGroupWhenRuleIsPublished() {

        List<ConditionGroup> conditionGroups = List.of(new ConditionGroup(LogicalOperator.AND));
        RuleDefinition ruleDefinition = RuleDefinition.restore(
                1L,
                "ruleDefintiion1",
                10,
                PUBLISHED,
                1,
                conditionGroups,
                UUID.randomUUID()



        );

        ConditionGroup conditionGroup = new ConditionGroup(LogicalOperator.AND);

        assertThrows(IllegalStateException.class, () -> ruleDefinition.addConditionGroup(conditionGroup));


    }

    @Test
    void shouldRejectAddingConditionGroupWhenGroupIsNull() {

        String name = "ruleDefintion1";
        int priority = 10;

        RuleDefinition ruleDefinition = new RuleDefinition(name, priority);

        assertThrows(IllegalArgumentException.class, () -> ruleDefinition.addConditionGroup(null));


    }

    @Test
    void shouldApproveRuleDefinition() {

        RuleCondition ruleCondition = new RuleCondition(
                "amount",
                GREATER_THAN,
                DECIMAL,
                "10000"
        );

       ConditionGroup conditionGroup =  new ConditionGroup(LogicalOperator.AND);
       conditionGroup.addCondition(ruleCondition);
        List<ConditionGroup> conditionGroups = List.of(conditionGroup);

        RuleDefinition ruleDefinition1 = RuleDefinition.restore(
                1L,
                "ruleDefintiion1",
                10,
                DRAFT,
                1,
                conditionGroups,
                UUID.randomUUID()
        );



        ruleDefinition1.approve();
        assertEquals(APPROVED, ruleDefinition1.getStatus());



    }

    @Test
    void shouldNotApproveRuleDefinitionWithStatusNoDraft() {


        List<ConditionGroup> conditionGroups = new ArrayList<>();

        RuleDefinition ruleDefinition = RuleDefinition.restore(
                1L,
                "ruleDefintiion1",
                10,
                PUBLISHED,
                1,
                conditionGroups,
                UUID.randomUUID()
        );

        assertThrows(IllegalStateException.class, () -> ruleDefinition.approve());

    }

    @Test
    void shouldNotApproveRuleDefinitionWithConditionGroupsIsEmpty() {
        List<ConditionGroup> conditionGroups = List.of();

        RuleDefinition ruleDefinition = RuleDefinition.restore(
                1L,
                "ruleDefintiion1",
                10,
                DRAFT,
                1,
                conditionGroups,
                UUID.randomUUID()
        );

        assertThrows(IllegalStateException.class, () -> ruleDefinition.approve());

    }

    @Test
    void shouldNotApproveRuleDefinitionWithConditionIsEmpty() {

       ConditionGroup conditionGroup = new ConditionGroup(LogicalOperator.AND);
       List<ConditionGroup> conditionGroups = List.of(conditionGroup);

        RuleDefinition ruleDefinition = RuleDefinition.restore(
                1L,
                "ruleDefintiion1",
                10,
                DRAFT,
                1,
                conditionGroups,
                UUID.randomUUID()
        );

        assertThrows(IllegalStateException.class, () -> ruleDefinition.approve());
    }

    @Test
    void shouldPublishRuleDefinition() {

        RuleCondition ruleCondition = new RuleCondition(
                "amount",
                GREATER_THAN,
                DECIMAL,
                "10000"
        );

        ConditionGroup conditionGroup =  new ConditionGroup(LogicalOperator.AND);
        conditionGroup.addCondition(ruleCondition);
        List<ConditionGroup> conditionGroups = List.of(conditionGroup);

        RuleDefinition ruleDefinition1 = RuleDefinition.restore(
                1L,
                "ruleDefintiion1",
                10,
                APPROVED,
                1,
                conditionGroups,
                UUID.randomUUID()
        );

        ruleDefinition1.publish();
        assertEquals(PUBLISHED, ruleDefinition1.getStatus());
    }

    @Test
    void shouldNotPublishRuleDefinitionWithStatusNoAproved() {

        List<ConditionGroup> conditionGroups = new ArrayList<>();

        RuleDefinition ruleDefinition = RuleDefinition.restore(
                1L,
                "ruleDefintiion1",
                10,
                DRAFT,
                1,
                conditionGroups,
                UUID.randomUUID()
        );

        assertThrows(IllegalStateException.class, () -> ruleDefinition.publish());

    }

    @Test
    void shouldRetireRuleDefinition() {

        RuleCondition ruleCondition = new RuleCondition(
                "amount",
                GREATER_THAN,
                DECIMAL,
                "10000"
        );

        ConditionGroup conditionGroup =  new ConditionGroup(LogicalOperator.AND);
        conditionGroup.addCondition(ruleCondition);
        List<ConditionGroup> conditionGroups = List.of(conditionGroup);

        RuleDefinition ruleDefinition1 = RuleDefinition.restore(
                1L,
                "ruleDefintiion1",
                10,
                PUBLISHED,
                1,
                conditionGroups,
                UUID.randomUUID()
        );

        ruleDefinition1.retire();
        assertEquals(RETIRED, ruleDefinition1.getStatus());
    }

    @Test
    void shouldNotRetireRuleDefinitionWithStatusNoPublished() {

        List<ConditionGroup> conditionGroups = new ArrayList<>();

        RuleDefinition ruleDefinition = RuleDefinition.restore(
                1L,
                "ruleDefintiion1",
                10,
                APPROVED,
                1,
                conditionGroups,
                UUID.randomUUID()
        );

        assertThrows(IllegalStateException.class, () -> ruleDefinition.retire());

    }

    @Test
    void shouldCreateNewVersionFromPublishedRule() {

        UUID ruleKey = UUID.randomUUID();

        RuleCondition condition = new RuleCondition(
                "amount",
                GREATER_THAN,
                DECIMAL,
                "10000"
        );

        ConditionGroup originalGroup =
                new ConditionGroup(LogicalOperator.AND);

        originalGroup.addCondition(condition);

        RuleDefinition originalRule =
                RuleDefinition.restore(
                        1L,
                        "ruleDefinition1",
                        10,
                        PUBLISHED,
                        1,
                        List.of(originalGroup),
                        ruleKey
                );

        RuleDefinition newVersion =
                originalRule.createNewVersion();


        ConditionGroup copiedGroup =
                newVersion.getConditionGroups().get(0);

        assertAll(
                () -> assertNull(newVersion.getId()),

                () -> assertEquals(
                        DRAFT,
                        newVersion.getStatus()
                ),

                () -> assertEquals(
                        originalRule.getVersion() + 1,
                        newVersion.getVersion()
                ),

                () -> assertEquals(
                        originalRule.getRuleKey(),
                        newVersion.getRuleKey()
                ),

                () -> assertEquals(
                        originalRule.getName(),
                        newVersion.getName()
                ),

                () -> assertEquals(
                        originalRule.getPriority(),
                        newVersion.getPriority()
                ),

                () -> assertEquals(
                        originalRule.getConditionGroups().size(),
                        newVersion.getConditionGroups().size()
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
                        originalGroup.getConditions().size(),
                        copiedGroup.getConditions().size()
                ),

                () -> assertEquals(
                        PUBLISHED,
                        originalRule.getStatus()
                ),

                () -> assertEquals(
                        1,
                        originalRule.getVersion()
                )
        );
    }

    @ParameterizedTest
    @EnumSource(
            value = RuleStatus.class,
            names = {"DRAFT", "APPROVED", "RETIRED"}
    )
    void shouldRejectCreatingNewVersionWhenRuleIsNotPublished(
            RuleStatus invalidStatus
    ) {

        RuleDefinition ruleDefinition =
                RuleDefinition.restore(
                        1L,
                        "ruleDefinition1",
                        10,
                        invalidStatus,
                        1,
                        List.of(),
                        UUID.randomUUID()
                );

        assertThrows(
                IllegalStateException.class,
                ruleDefinition::createNewVersion
        );
    }



}