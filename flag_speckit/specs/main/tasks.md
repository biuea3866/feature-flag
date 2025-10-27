# Tasks: Core Feature Flag System

**Input**: Design documents from `/specs/main/`

## Phase 1: Setup (Shared Infrastructure)

- [X] T001 Initialize Spring Boot project with Gradle and Kotlin in `backend/`
- [X] T002 Create `docker-compose.yml` for PostgreSQL and Redis services
- [X] T003 Configure database connection in `backend/src/main/resources/application.properties`
- [X] T004 Create basic project structure: `frontend/` and `tests/` directories

## Phase 2: Foundational (Blocking Prerequisites)

- [X] T005 Add Flyway for database migrations in `backend/`
- [X] T006 Create initial Flyway migration for all entities in `backend/src/main/resources/db/migration/V1__Initial_schema.sql`
- [X] T007 [P] Create JPA entity for `FeatureFlag` in `backend/src/main/kotlin/com/example/flag/repository/FeatureFlag.kt`
- [X] T008 [P] Create JPA entity for `Rule` in `backend/src/main/kotlin/com/example/flag/repository/Rule.kt`
- [X] T009 [P] Create JPA entity for `Workspace` in `backend/src/main/kotlin/com/example/flag/repository/Workspace.kt`
- [X] T010 [P] Create JPA entity for `Attribute` in `backend/src/main/kotlin/com/example/flag/repository/Attribute.kt`
- [X] T011 [P] Create JPA entity for `AuditLog` in `backend/src/main/kotlin/com/example/flag/repository/AuditLog.kt`
- [X] T012 Create Spring Data JPA repositories for all entities in `backend/src/main/kotlin/com/example/flag/repository/`

## Phase 3: User Story 1 - Flag Creation and Basic Toggle (Priority: P1) 🎯 MVP

**Goal**: As a developer, I want to create a new feature flag and be able to turn it on or off globally.
**Independent Test**: Can be tested by creating a flag via an API, and then verifying its state (on/off) is respected by the evaluation endpoint.

- [X] T013 [US1] Implement `POST /flags` and `PUT /flags/{flag_name}` endpoints in `backend/src/main/kotlin/com/example/flag/controller/FlagController.kt`
- [X] T014 [US1] Implement service logic for creating and toggling flags in `backend/src/main/kotlin/com/example/flag/service/FlagService.kt`
- [X] T015 [US1] Implement basic evaluation logic for `GET /evaluate` in `backend/src/main/kotlin/com/example/flag/service/EvaluationService.kt`
- [X] T016 [US1] Create Kotest test for basic flag creation and toggling in `backend/src/test/kotlin/com/example/flag/FlagServiceTest.kt`

## Phase 4: User Story 2 - Percentage-based Rollout (Priority: P2)

**Goal**: As a product manager, I want to release a feature to a small percentage of workspaces.
**Independent Test**: Can be tested by setting a flag to a 10% rollout and then evaluating it for a sample of 100 workspaces.

- [X] T017 [US2] Update `Rule` entity and API to support percentage rollouts
- [X] T018 [US2] Add percentage rollout logic to `EvaluationService` in `backend/src/main/kotlin/com/example/flag/service/EvaluationService.kt`
- [X] T019 [US2] Create Kotest test for percentage-based rollouts in `backend/src/test/kotlin/com/example/flag/EvaluationServiceTest.kt`

## Phase 5: User Story 3 - Attribute-based Targeting (Priority: P3)

**Goal**: As a developer, I want to target a feature to workspaces with a specific attribute.
**Independent Test**: Can be tested by creating a rule to target workspaces with a specific attribute and verifying the evaluation.

- [X] T020 [US3] Update `Rule` entity and API to support attribute-based targeting
- [X] T021 [US3] Add attribute-based targeting logic to `EvaluationService` in `backend/src/main/kotlin/com/example/flag/service/EvaluationService.kt`
- [X] T022 [US3] Create Kotest test for attribute-based targeting in `backend/src/test/kotlin/com/example/flag/EvaluationServiceTest.kt`

## Phase 6: Polish & Cross-Cutting Concerns

- [X] T023 Implement audit logging for all flag changes in `backend/src/main/kotlin/com/example/flag/service/AuditService.kt`
- [X] T024 [P] Create scenario tests using `.http` files for all API endpoints in `backend/src/test/resources/scenarios.http`
- [X] T025 [P] Implement basic frontend UI for viewing and toggling flags in `frontend/`

## Dependencies & Execution Order

- **User Story 1 (P1)**: Can start after Foundational (Phase 2).
- **User Story 2 (P2)**: Depends on User Story 1.
- **User Story 3 (P3)**: Depends on User Story 1.
