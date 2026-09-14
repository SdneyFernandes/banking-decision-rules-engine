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
- [ ] Java ↔ PostgreSQL connection
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

Contains technical implementations such as PostgreSQL, JPA/Hibernate, Spring Data and Flyway.

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
- Maven
- Git

Planned for the current foundation phase:

- PostgreSQL
- SQL
- Flyway
- JPA
- Hibernate
- Spring Data

Testing, messaging, security, observability, distributed systems and cloud infrastructure will be introduced in later versions when they solve a concrete project need.

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

**Current next step:** Phase 10.6 — Java ↔ PostgreSQL Connection.
