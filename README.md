# Banking Decision Rules Engine

A configurable backend rules engine for banking decision flows, built incrementally with **Java 21** and **Spring Boot**.

This repository is a long-term software engineering project. The goal is to evolve the same system as new backend concepts are studied and applied, instead of creating isolated tutorial projects.

## Current Status

**Phase 10 — Foundation & Persistence**

Progress:

- [x] Java 21 project bootstrap
- [x] Spring Boot application
- [x] Maven build and executable JAR
- [x] Initial architectural boundaries
- [x] Application configuration
- [x] PostgreSQL development environment
- [x] Java ↔ PostgreSQL connection
- [ ] Flyway migrations
- [ ] JPA persistence model
- [ ] Repository layer
- [ ] First persistence flow

## Architecture

The project starts with four explicit boundaries:

```text
API
 ↓
Application
 ↓
Domain

Infrastructure
 ↓
Application / Domain
```

### Domain

Contains business concepts and business rules. The domain should remain as independent as possible from HTTP, Spring MVC, persistence frameworks and infrastructure details.

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

Contains technical implementations such as PostgreSQL, JDBC, JPA/Hibernate, Spring Data and Flyway.

## Domain Direction

The engine is being designed around concepts such as:

```text
RuleDefinition
ConditionGroup
RuleCondition
Criteria
Fact
Evaluation
Decision
```

Initial rule lifecycle:

```text
DRAFT
  ↓
APPROVED
  ↓
PUBLISHED
  ↓
RETIRED
```

Initial evaluation strategy:

```text
Priority + First Match Wins
```

## Current Stack

- Java 21
- Spring Boot 4.1.1
- Spring MVC
- Spring JDBC
- HikariCP
- PostgreSQL 15
- Docker Compose
- Maven
- Git

Planned for the current foundation phase:

- SQL schema versioning with Flyway
- JPA
- Hibernate
- Spring Data

Testing, messaging, security, observability, distributed systems and cloud infrastructure will be introduced in later versions when they solve a concrete project need.

## PostgreSQL Development Environment

PostgreSQL runs locally in Docker Compose.

The development setup exposes the database through:

```text
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
```

Environment-specific values are stored outside version control.

Create the local environment file from the example:

```bash
cp .env.example .env
```

Start PostgreSQL:

```bash
docker compose up -d
docker compose ps
```

The Compose configuration uses the local `.env` file for PostgreSQL initialization and host port mapping.

## Spring Profiles and Database Configuration

Common application configuration lives in:

```text
src/main/resources/application.yml
```

Local database configuration lives in:

```text
src/main/resources/application-local.yml
```

The local profile configures the Spring `DataSource` using environment variables:

```text
POSTGRES_DB
POSTGRES_USER
POSTGRES_PASSWORD
POSTGRES_PORT
```

The application does not read the project `.env` file directly. When running locally from Bash, export its values into the process environment first:

```bash
set -a
source .env
set +a
```

Then activate the local Spring profile:

```bash
export SPRING_PROFILES_ACTIVE=local
```

Run the application:

```bash
./mvnw spring-boot:run
```

## Database Connection Validation

The project includes an explicit PostgreSQL integration test:

```text
PostgresConnectionIT
```

The test starts the Spring context with the `local` profile, obtains a real JDBC connection from the configured `DataSource`, executes:

```sql
SELECT 1
```

and validates the returned value.

This verifies the complete path:

```text
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
```

Run the integration test with PostgreSQL running and the environment variables exported:

```bash
set -a
source .env
set +a

./mvnw -Dtest=PostgresConnectionIT test
```

## Test Configuration

Standard application-context tests use the `test` profile:

```text
src/test/resources/application-test.yml
```

The test profile excludes database auto-configuration so that the regular test suite does not depend on a developer's local PostgreSQL instance.

Run the standard test suite with:

```bash
./mvnw test
```

The current separation is intentional:

```text
Standard tests
→ profile: test
→ no external PostgreSQL required

PostgreSQL integration test
→ profile: local
→ real PostgreSQL required
```

A more isolated database-testing strategy such as Testcontainers can be introduced later when persistence behavior becomes part of the test scope.

## Development Roadmap

```text
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
```

## Engineering Approach

- Understand the concept before abstracting it.
- Keep business rules separate from infrastructure where practical.
- Build in small, verifiable increments.
- Use Git continuously during development.
- Test each increment before moving forward.
- Update architecture and documentation when implementation reveals a better design.
- Add technologies because the system needs them, not to decorate the stack.

## Repository Workflow

Development follows small changes with clear Git history:

```text
understand → implement → inspect diff → test → commit → review → next increment
```

Commit messages follow a simple Conventional Commits style, for example:

```text
chore: configure application settings
feat: add initial database migration
fix: correct rule persistence mapping
refactor: reorganize persistence adapter
docs: update architecture documentation
test: add repository integration coverage
```

---

**Current next step:** Phase 10.7 — Flyway & Schema Versioning.
