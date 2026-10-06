# Phase 11 — Rule Configuration

Phase 11 introduces the first real business behavior of the Banking Decision Rules Engine.

## Goal

The phase goal is to make rule configuration explicit and safe before building the evaluation engine.

The system must be able to:

~~~text
create a Rule
↓
configure ConditionGroups
↓
configure RuleConditions
↓
approve the configuration
↓
publish the Rule
↓
create a new version later
↓
retire the previous published version
~~~

## Domain model

~~~text
RuleDefinition
    │
    ├── id
    ├── ruleKey
    ├── version
    ├── name
    ├── priority
    ├── status
    │
    └── ConditionGroup
            │
            ├── LogicalOperator
            │
            └── RuleCondition
                    ├── factKey
                    ├── ComparisonOperator
                    ├── ValueType
                    └── expectedValue
~~~

RuleDefinition is the aggregate root for configuration.

RuleCondition describes a comparison. It does not evaluate runtime data yet. Evaluation belongs to Phase 12.

Example:

~~~text
factKey       = amount
operator      = GREATER_THAN
valueType     = DECIMAL
expectedValue = 10000

description:
amount > 10000
~~~

## Lifecycle

A new Rule starts as:

~~~text
id       = null
status   = DRAFT
version  = 1
ruleKey  = generated UUID
~~~

The supported lifecycle is:

~~~text
DRAFT
  ↓ approve()
APPROVED
  ↓ publish()
PUBLISHED
  ↓ retire()
RETIRED
~~~

Configuration can only be changed while the Rule is DRAFT.

Approval requires:

~~~text
at least one ConditionGroup
and
every ConditionGroup has at least one RuleCondition
~~~

## Versioning

Published rules are immutable from the configuration perspective.

A new version is created instead of editing the published version:

~~~text
v1
id      = 10
ruleKey = ABC
status  = PUBLISHED

createNewVersion()

v2
id      = null
ruleKey = ABC
status  = DRAFT
version = v1 + 1
~~~

The new version preserves the logical identity through ruleKey while receiving a new persistence id after it is saved.

ConditionGroups are copied into new mutable group instances. RuleCondition values can be reused because the current RuleCondition model is immutable.

When v2 is eventually published:

~~~text
before

v1 PUBLISHED
v2 APPROVED

publish(v2)

after

v1 RETIRED
v2 PUBLISHED
~~~

This keeps historical versions instead of overwriting them.

## Application boundary

The application layer exposes the RuleRepository port:

~~~text
RuleUseCases
    ↓
RuleRepository
    ↓
JpaRuleRepositoryAdapter
    ↓
Spring Data JPA
    ↓
PostgreSQL
~~~

Responsibilities are intentionally separated:

~~~text
Domain
→ business invariants and lifecycle

Application
→ use-case orchestration

Infrastructure
→ technical persistence
~~~

Implemented application operations:

~~~text
create
approve
publish
addConditionGroup
createNewVersion
~~~

## Persistence changes

Flyway migration V4 introduces business versioning fields to rule_definition:

~~~text
version INTEGER NOT NULL DEFAULT 1
rule_key UUID NOT NULL
~~~

Constraints:

~~~text
CHECK (version > 0)
UNIQUE (rule_key, version)
~~~

The unique pair allows multiple versions of the same logical Rule while preventing duplicate version numbers inside that family.

## Testing

Permanent unit tests added in this phase:

~~~text
RuleConditionTest
ConditionGroupTest
RuleDefinitionTest
RuleUseCasesTest
~~~

The domain tests cover:

~~~text
construction invariants
configuration invariants
lifecycle transitions
invalid transitions
version creation
immutable collection exposure
~~~

The application tests use Mockito only at the RuleRepository boundary and cover:

~~~text
create orchestration
approve orchestration
publish without a previous version
publish while retiring a previous version
not-found behavior
condition-group persistence orchestration
new-version persistence orchestration
reload failure after version creation
~~~

The domain objects themselves are real objects in application tests rather than mocks.

## Deferred work

Repository and JPA integration tests are intentionally deferred to Phase 15 — Tests & Quality.

That phase will validate the real infrastructure with:

~~~text
Spring
JPA / Hibernate
Flyway
PostgreSQL
Testcontainers
~~~

The Phase 11 unit suite therefore verifies business behavior and application orchestration without pretending that mocked persistence proves database behavior.

## Phase result

Phase 11 is closed with a configurable and versioned rule lifecycle.

The next phase is:

~~~text
Phase 12 — Rule Engine Core
~~~

Phase 12 will introduce Facts and execute configured RuleConditions against runtime values.
