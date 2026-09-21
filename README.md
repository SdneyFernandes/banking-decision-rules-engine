# Banking Decision Rules Engine

A configurable backend rules engine for banking decision flows, built incrementally with **Java 21** and **Spring Boot**.

This repository is a long-term software engineering project. The goal is to evolve the same system as new backend concepts are studied and applied, instead of creating isolated tutorial projects.

## Current Status

**Phase 10 — Foundation & Persistence ✅**

~~~text
[✅] 10.1  Bootstrap
[✅] 10.2  Project anatomy
[✅] 10.3  Architecture and packages
[✅] 10.4  Application configuration
[✅] 10.5  PostgreSQL with Docker
[✅] 10.6  Java ↔ PostgreSQL connection
[✅] 10.7  Flyway and schema versioning
[✅] 10.8  JPA Entities + Enums
[✅] 10.9  JPA relationship behavior and mapping validation
[✅] 10.10 Spring Data JPA repositories
[✅] 10.11 First persistence flow
[✅] 10.12 Persistence validation
[✅] 10.13 Phase review and documentation
~~~

Phase 10 is complete. The persistence flow was exercised locally with exploratory integration tests while learning EntityManager, repositories, relationships, flush, clear and dirty checking. Those temporary exploratory tests were intentionally not retained in the repository. Durable, broader persistence coverage remains part of Phase 15 — Tests & Quality.

## Architecture

The project starts with four explicit boundaries:

~~~text
API
 ↓
Application
 ↓
Domain

Infrastructure
 ↓
Application / Domain
~~~

### Domain

Contains business concepts and business rules. The domain should remain as independent as possible from HTTP, Spring MVC, persistence frameworks and infrastructure details.

Current domain enums used by the persistence model:

~~~text
RuleStatus
LogicalOperator
ComparisonOperator
ValueType
~~~

### Application

Orchestrates use cases and coordinates the domain with required ports.

Planned use cases include:

- Create Rule
- Approve Rule
- Publish Rule
- Evaluate Decision
- Get Evaluation

### API

Represents the HTTP boundary of the system.

Future responsibilities include controllers, request/response DTOs, validation and HTTP error handling.

### Infrastructure

Contains technical implementations such as PostgreSQL, JDBC, Flyway, JPA/Hibernate and Spring Data.

JPA entities live under the infrastructure boundary instead of the domain so that business concepts do not depend on persistence annotations.

## Domain Direction

The current rule configuration model is:

~~~text
RuleDefinition
    │
    │ 1:N
    ▼
ConditionGroup
    │
    │ 1:N
    ▼
RuleCondition
~~~

A RuleCondition represents one concrete comparison:

~~~text
factKey + operator + expectedValue

amount + GREATER_THAN + 10000
country + NOT_EQUALS + BR
~~~

ValueType tells the engine how the values must be interpreted:

~~~text
STRING
INTEGER
DECIMAL
BOOLEAN
DATE
~~~

The separate Criteria abstraction was intentionally removed from the MVP because it did not yet solve a concrete problem. If reusable criteria or a centralized fact catalog becomes necessary later, the model can evolve through a new migration and domain change.

Other planned concepts include:

~~~text
Fact
Evaluation
Decision
~~~

Evaluation persistence is intentionally deferred until the evaluation flow is introduced in Phase 13 rather than creating unused tables and entities during the foundation phase.

Initial rule lifecycle:

~~~text
DRAFT
  ↓
APPROVED
  ↓
PUBLISHED
  ↓
RETIRED
~~~

Initial evaluation strategy:

~~~text
Priority + First Match Wins
~~~

Priority currently represents evaluation order, not a score that is summed across rules.

## Current Stack

- Java 21
- Spring Boot 4.1.1
- Spring MVC
- Spring JDBC
- Spring Data JPA
- Hibernate ORM
- HikariCP
- PostgreSQL 15
- Flyway
- Docker Compose
- Maven
- Git

Testing, messaging, security, observability, distributed systems and cloud infrastructure will be introduced when they solve a concrete project need.

## PostgreSQL Development Environment

PostgreSQL runs locally in Docker Compose.

The current connection path is:

~~~text
Application
    ↓
DataSource
    ↓
HikariCP
    ↓
JDBC
    ↓
PostgreSQL JDBC Driver
    ↓
localhost:5433
    ↓
Docker
    ↓
PostgreSQL:5432
~~~

Environment-specific values are stored outside version control.

Create the local environment file from the example:

~~~bash
cp .env.example .env
~~~

Start PostgreSQL:

~~~bash
docker compose up -d
docker compose ps
~~~

The Compose configuration uses the local .env file for PostgreSQL initialization and host port mapping.

## Spring Profiles and Database Configuration

Common application configuration lives in:

~~~text
src/main/resources/application.yml
~~~

Local database configuration lives in:

~~~text
src/main/resources/application-local.yml
~~~

The local profile configures the Spring DataSource using environment variables:

~~~text
POSTGRES_DB
POSTGRES_USER
POSTGRES_PASSWORD
POSTGRES_PORT
~~~

The application does not read the project .env file directly. Export its values into the shell process before running locally:

~~~bash
set -a
source .env
set +a
~~~

Activate the local Spring profile:

~~~bash
export SPRING_PROFILES_ACTIVE=local
~~~

Run the application:

~~~bash
./mvnw spring-boot:run
~~~

## Flyway Schema Versioning

Flyway owns database schema evolution.

Current migrations:

~~~text
V1__create_rule_definition.sql
    ↓
V2__create_condition_group.sql
    ↓
V3__create_rule_condition.sql
~~~

The resulting configuration schema is:

~~~text
rule_definition
    │
    │ 1:N
    ▼
condition_group
    │
    │ 1:N
    ▼
rule_condition
~~~

Flyway stores applied migration metadata in:

~~~text
flyway_schema_history
~~~

On startup, Flyway validates existing migrations, checks the current schema version and executes only pending migrations.

The project was validated from an empty PostgreSQL volume, proving that the complete configuration schema can be reconstructed automatically from V1 → V2 → V3.

### Migration rule

Once a versioned migration has been applied in a shared or permanent environment, it is treated as immutable.

Future schema changes must be introduced through a new version:

~~~text
V1 applied
V2 applied
V3 applied

new schema change
→ V4
~~~

This keeps schema evolution reproducible across environments.

## JPA Persistence Model

The persistence model maps the Flyway-managed schema into Java.

~~~text
PostgreSQL                     JPA / Hibernate

rule_definition          ↔     RuleDefinitionEntity
condition_group          ↔     ConditionGroupEntity
rule_condition           ↔     RuleConditionEntity
~~~

### RuleDefinitionEntity

Maps:

~~~text
id          ↔ Long
name        ↔ String
status      ↔ RuleStatus
priority    ↔ Integer
created_at  ↔ OffsetDateTime
~~~

The PostgreSQL identity column generates id.

created_at is also database-owned through:

~~~sql
DEFAULT CURRENT_TIMESTAMP
~~~

The JPA mapping marks it as generated and excludes it from application INSERT/UPDATE statements.

### ConditionGroupEntity

Represents a group of conditions joined by:

~~~text
AND
OR
~~~

It owns the foreign key:

~~~text
condition_group.rule_definition_id
~~~

and maps the child-to-parent direction with @ManyToOne.

### RuleConditionEntity

Represents one executable comparison configuration:

~~~text
factKey
operator
valueType
expectedValue
~~~

Example:

~~~text
factKey       = amount
operator      = GREATER_THAN
valueType     = DECIMAL
expectedValue = 10000

→ amount > 10000
~~~

The factKey identifies which runtime fact must be read. The expectedValue represents the configured comparison target. ValueType tells the future engine how to interpret the values before applying the operator.

### Enum persistence

Enums are persisted with @Enumerated(EnumType.STRING).

This keeps Java enum names aligned with the VARCHAR + CHECK constraints defined by Flyway.

The following contract must therefore remain consistent:

~~~text
Java enum constant
        ↕
persisted String
        ↕
PostgreSQL CHECK constraint
~~~

For example, GREATER_THAN_OR_EQUALS must have the exact same representation in Java and in the database constraint.

## Flyway and Hibernate Responsibilities

Flyway and Hibernate are intentionally used for different jobs:

~~~text
Flyway
→ creates and evolves the physical schema

Hibernate
→ maps Java objects to that schema
→ validates compatibility
→ later performs persistence operations
~~~

The local profile uses:

~~~yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
~~~

Hibernate therefore validates the schema but does not create or update it.

The startup sequence is conceptually:

~~~text
Application starts
      ↓
Flyway validates/applies migrations
      ↓
PostgreSQL schema is ready
      ↓
Hibernate reads entity mappings
      ↓
Hibernate validates mappings against schema
      ↓
Application starts
~~~

A deliberate experiment with an invalid column mapping confirmed that Hibernate fails startup when an entity expects a column that does not exist.

Schema validation does not validate every business-level database constraint. For example, a VARCHAR column may structurally validate even if a Java enum constant does not match a PostgreSQL CHECK value. Persistence tests are therefore still necessary.

## JPA Relationships

The current mappings are bidirectional:

~~~text
RuleDefinitionEntity
    ↓ @OneToMany(mappedBy = "ruleDefinition")

ConditionGroupEntity
    ↑ @ManyToOne + @JoinColumn
    ↓ @OneToMany(mappedBy = "conditionGroup")

RuleConditionEntity
    ↑ @ManyToOne + @JoinColumn
~~~

The database still contains only two physical foreign keys:

~~~text
condition_group.rule_definition_id
rule_condition.condition_group_id
~~~

The side containing @JoinColumn is the owning side because it controls the foreign key.

mappedBy points to the Java attribute on the owning side, not to the SQL column name.

Associations use lazy loading explicitly and no cascade behavior has been introduced yet. Cascade rules will only be added when persistence behavior makes the required lifecycle clear.

## Database Connection Validation

The project includes an explicit PostgreSQL integration test:

~~~text
PostgresConnectionIT
~~~

The test starts the Spring context with the local profile, obtains a real JDBC connection from the configured DataSource, executes:

~~~sql
SELECT 1
~~~

and validates the result.

This verifies:

~~~text
Spring Boot
    ↓
DataSource
    ↓
HikariCP
    ↓
PostgreSQL JDBC Driver
    ↓
Docker
    ↓
PostgreSQL
~~~

Run the integration test with PostgreSQL running and environment variables exported:

~~~bash
set -a
source .env
set +a

./mvnw -Dtest=PostgresConnectionIT test
~~~

## Test Configuration

Standard application-context tests use the test profile:

~~~text
src/test/resources/application-test.yml
~~~

The test profile excludes database auto-configuration so the regular test suite does not depend on a developer's PostgreSQL instance.

Run the standard test suite with:

~~~bash
./mvnw test
~~~

The JPA persistence model was also validated through the full Maven build:

~~~bash
./mvnw clean install
~~~

Current separation:

~~~text
Standard tests
→ profile: test
→ no external PostgreSQL required

PostgreSQL integration / local startup
→ profile: local
→ Flyway + Hibernate validate against real PostgreSQL
~~~

A more isolated database-testing strategy such as Testcontainers is planned when persistence testing expands.

## Phase 10 — Foundation & Persistence

The purpose of Phase 10 is to prepare the application foundation and persistence layer. Business behavior such as creating, approving and publishing rules belongs to Phase 11.

### 10.1 — Bootstrap ✅

Java 21, Spring Boot, Maven Wrapper, executable build, initial test and application startup.

### 10.2 — Project Anatomy ✅

Project structure, pom.xml, application entry point, resources, tests, build output and Spring Boot startup model.

### 10.3 — Architecture and Packages ✅

Initial boundaries:

~~~text
api
application
domain
infrastructure
~~~

### 10.4 — Application Configuration ✅

YAML configuration, profiles, environment variables and externalized local configuration.

### 10.5 — PostgreSQL with Docker ✅

Docker Compose, PostgreSQL container, database/user configuration, host/container ports and persistent volume.

### 10.6 — Java ↔ PostgreSQL Connection ✅

Spring JDBC, PostgreSQL JDBC driver, DataSource, HikariCP and a real physical connection test.

### 10.7 — Flyway and Schema Versioning ✅

Flyway integration, migration history, checksums, incremental migrations, foreign keys, CHECK constraints and reconstruction from an empty database.

### 10.8 — JPA Entities + Enums ✅

Spring Data JPA / Hibernate integration, enums, entity-to-table mapping, identity generation, database-generated timestamps, enum persistence, initial @ManyToOne / @OneToMany mappings and Hibernate schema validation.

### 10.9 — JPA Relationship Behavior and Mapping Validation ✅

The structural mappings were exercised to understand owning side vs inverse side, bidirectional navigation, persistence context, flush, clear and lazy associations.

~~~text
@ManyToOne + @JoinColumn
→ owning side / controls the foreign key

@OneToMany(mappedBy = ...)
→ inverse side / mirrors the same relationship
~~~

### 10.10 — Spring Data JPA Repositories ✅

The infrastructure now contains one technical Spring Data repository per persisted entity:

~~~text
RuleDefinitionJpaRepository
ConditionGroupJpaRepository
RuleConditionJpaRepository
~~~

Each repository extends JpaRepository<Entity, Long> and is implemented at runtime by Spring Data JPA.

A domain/application persistence port was deliberately not introduced yet. Phase 11 will create real business use cases first, which will reveal the persistence contract the application actually needs.

### 10.11 — First Persistence Flow ✅

The complete structure was exercised locally:

~~~text
RuleDefinitionEntity
↓
ConditionGroupEntity
↓
RuleConditionEntity
↓
Spring Data repositories
↓
EntityManager / Persistence Context
↓
Hibernate
↓
PostgreSQL
~~~

This work covered save/findById, transaction boundaries, flush, clear, managed vs detached state and dirty checking.

### 10.12 — Persistence Validation ✅

The persistence flow and relationships were validated locally against the real PostgreSQL development database.

The temporary exploratory JPA flow tests used during this learning step were removed afterward and are not part of the permanent test suite. The existing PostgreSQL connection validation remains in the repository, while durable repository/integration coverage is planned for Phase 15.

### 10.13 — Phase Review and Documentation ✅

The database was rebuilt from an empty Docker volume, Flyway reapplied V1 → V2 → V3, Hibernate validated the mappings, the persistence flow was exercised, and the Maven build completed successfully.

Phase 10 is therefore closed with a reproducible persistence foundation.

## Development Roadmap

~~~text
10 — Foundation & Persistence
 ↓
11 — Rule Configuration
 ↓
12 — Rule Engine Core
 ↓
13 — Evaluation Flow
 ↓
14 — API & Error Handling
 ↓
15 — Tests & Quality
 ↓
16 — Audit & Explainability
 ↓
17 — Performance
 ↓
18 — Observability
 ↓
19 — Security & Robustness
 ↓
20 — Delivery & Portfolio
~~~

The practical progression is:

~~~text
Block 1 — Make it exist
10 Foundation/Persistence
→ 11 Rule Configuration
→ 12 Engine Core
→ 13 Evaluation Flow

Block 2 — Make it reliable
14 API
→ 15 Tests
→ 16 Audit

Block 3 — Make it production-oriented
17 Performance
→ 18 Observability
→ 19 Security
→ 20 Delivery
~~~

## Engineering Approach

- Understand the concept before abstracting it.
- Keep business rules separate from infrastructure where practical.
- Build in small, verifiable increments.
- Use Git continuously during development.
- Test each increment before moving forward.
- Update architecture and documentation when implementation reveals a better design.
- Add technologies because the system needs them, not to decorate the stack.
- Prefer explicit schema migrations over implicit ORM schema changes.
- Delay abstractions until a concrete requirement justifies them.

## Repository Workflow

Development follows small changes with clear Git history:

~~~text
understand → implement → inspect diff → test → commit → review → next increment
~~~

Commit messages follow a simple Conventional Commits style, for example:

~~~text
chore: configure application settings
feat: add initial database migration
feat: add JPA persistence model
fix: align enum persistence with database constraint
refactor: reorganize persistence adapter
docs: update architecture documentation
test: add repository integration coverage
~~~

---

**Current next step:** Phase 11 — Rule Configuration.
