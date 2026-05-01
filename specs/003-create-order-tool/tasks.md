# Tasks: Create Order Tool

**Input**: Design documents from `/specs/003-create-order-tool/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

**Tests**: Test tasks are REQUIRED. For every user story, write the failing automated tests before implementation tasks and keep them in the same phase.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Create the package and test support locations used by all stories.

- [ ] T001 Create order package directories at `src/main/java/com/cardozojavier/pocmcpserver/mcp/order/` and `src/test/java/com/cardozojavier/pocmcpserver/mcp/order/`
- [ ] T002 [P] Create reusable valid draft fixtures in `src/test/java/com/cardozojavier/pocmcpserver/mcp/order/OrderDraftTestFixtures.java`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Define shared request and validation types that every story uses.

**CRITICAL**: No user story work can begin until this phase is complete.

- [ ] T003 Create request draft records for standard, optional, authentication, recurring, item, and processor context data in `src/main/java/com/cardozojavier/pocmcpserver/mcp/order/OrderRequestDraft.java`
- [ ] T004 [P] Create validation category and issue value types in `src/main/java/com/cardozojavier/pocmcpserver/mcp/order/ValidationIssue.java`
- [ ] T005 [P] Create validation response type with ready state, flow types, draft, missing fields, issues, warnings, and next steps in `src/main/java/com/cardozojavier/pocmcpserver/mcp/order/ValidationResult.java`
- [ ] T006 Create validation service skeleton without story-specific rules in `src/main/java/com/cardozojavier/pocmcpserver/mcp/order/OrderRequestValidator.java`

**Checkpoint**: Foundation ready - user story implementation can now begin.

---

## Phase 3: User Story 1 - Build a Standard Order Request (Priority: P1) MVP

**Goal**: Merchants can build and validate a complete standard order request draft without submitting an order.

**Independent Test**: Provide valid standard order inputs and confirm the tool returns a ready non-submitted draft; omit required fields and confirm all missing fields are reported.

### Tests for User Story 1

> Write these tests FIRST and confirm they fail before implementation.

- [ ] T007 [P] [US1] Add MCP contract test for `build_order_request` tool exposure and output fields in `src/test/java/com/cardozojavier/pocmcpserver/mcp/order/CreateOrderToolContractTests.java`
- [ ] T008 [P] [US1] Add standard complete, missing required, invalid code, invalid URL, invalid IP, and optional context tests in `src/test/java/com/cardozojavier/pocmcpserver/mcp/order/CreateOrderToolValidationTests.java`
- [ ] T009 [P] [US1] Add safe logging assertions for `build_order_request` readiness and issue counts in `src/test/java/com/cardozojavier/pocmcpserver/mcp/PaymentGatewayToolsLoggingTests.java`

### Implementation for User Story 1

- [ ] T010 [US1] Add `build_order_request` MCP tool method and validator dependency in `src/main/java/com/cardozojavier/pocmcpserver/mcp/PaymentGatewayTools.java`
- [ ] T011 [US1] Implement standard required field validation for money, notification URL, redirect URLs, user information, browser information, and domain in `src/main/java/com/cardozojavier/pocmcpserver/mcp/order/OrderRequestValidator.java`
- [ ] T012 [US1] Implement standard value validation for amount, ISO currency, ISO country, ISO language, public IPv4, email shape, and URLs in `src/main/java/com/cardozojavier/pocmcpserver/mcp/order/OrderRequestValidator.java`
- [ ] T013 [US1] Implement standard draft normalization and next-step messages without submission claims in `src/main/java/com/cardozojavier/pocmcpserver/mcp/order/OrderRequestValidator.java`
- [ ] T014 [US1] Log only tool name, readiness, flow types, and issue counts for standard drafts in `src/main/java/com/cardozojavier/pocmcpserver/mcp/PaymentGatewayTools.java`
- [ ] T015 [US1] Run `./mvnw test -Dtest=CreateOrderToolValidationTests,CreateOrderToolContractTests,PaymentGatewayToolsLoggingTests` for `src/test/java/com/cardozojavier/pocmcpserver/mcp/order/CreateOrderToolValidationTests.java`

**Checkpoint**: User Story 1 is independently functional and demoable as the MVP.

---

## Phase 4: User Story 2 - Validate Authentication-Enhanced Requirements (Priority: P2)

**Goal**: Merchants receive authentication-enhanced field requirements and guidance when the order context requires them.

**Independent Test**: Provide a region/context requiring enhanced authentication and confirm the tool requires authentication support details and the challenge-result redirect URL; provide complete details and confirm readiness.

### Tests for User Story 2

> Write these tests FIRST and confirm they fail before implementation.

- [ ] T016 [P] [US2] Add tests for region-triggered authentication requirements and missing challenge redirect in `src/test/java/com/cardozojavier/pocmcpserver/mcp/order/CreateOrderToolAuthenticationTests.java`
- [ ] T017 [P] [US2] Add tests for complete authentication details and non-guaranteed challenge warning in `src/test/java/com/cardozojavier/pocmcpserver/mcp/order/CreateOrderToolAuthenticationTests.java`

### Implementation for User Story 2

- [ ] T018 [US2] Implement authentication-required detection and missing authentication fields in `src/main/java/com/cardozojavier/pocmcpserver/mcp/order/OrderRequestValidator.java`
- [ ] T019 [US2] Implement challenge-result redirect validation and authentication detail preservation in `src/main/java/com/cardozojavier/pocmcpserver/mcp/order/OrderRequestValidator.java`
- [ ] T020 [US2] Add non-blocking warning that authentication fields support but do not guarantee a challenge in `src/main/java/com/cardozojavier/pocmcpserver/mcp/order/OrderRequestValidator.java`
- [ ] T021 [US2] Run `./mvnw test -Dtest=CreateOrderToolAuthenticationTests,CreateOrderToolValidationTests` for `src/test/java/com/cardozojavier/pocmcpserver/mcp/order/CreateOrderToolAuthenticationTests.java`

**Checkpoint**: User Stories 1 and 2 both work independently.

---

## Phase 5: User Story 3 - Build Recurring Order Requests (Priority: P3)

**Goal**: Merchants can build and validate initial, recurring, and final recurring request drafts with the correct sequence, token, period, and parent transaction rules.

**Independent Test**: Validate one initial recurrence, one recurring sequence, and one final sequence with correct conditional fields; confirm invalid minimum period, missing parent transaction, and invalid token generation are rejected.

### Tests for User Story 3

> Write these tests FIRST and confirm they fail before implementation.

- [ ] T022 [P] [US3] Add initial recurring setup tests for recurrence type, sequence, minimum period, and token generation in `src/test/java/com/cardozojavier/pocmcpserver/mcp/order/CreateOrderToolRecurringTests.java`
- [ ] T023 [P] [US3] Add recurring and final sequence tests for parent transaction requirement and token-generation rejection in `src/test/java/com/cardozojavier/pocmcpserver/mcp/order/CreateOrderToolRecurringTests.java`
- [ ] T024 [P] [US3] Add invalid minimum period and invalid recurring sequence tests in `src/test/java/com/cardozojavier/pocmcpserver/mcp/order/CreateOrderToolRecurringTests.java`

### Implementation for User Story 3

- [ ] T025 [US3] Implement recurring type, sequence, and minimum period validation in `src/main/java/com/cardozojavier/pocmcpserver/mcp/order/OrderRequestValidator.java`
- [ ] T026 [US3] Implement initial recurring token-generation rules in `src/main/java/com/cardozojavier/pocmcpserver/mcp/order/OrderRequestValidator.java`
- [ ] T027 [US3] Implement parent transaction and no-new-token rules for recurring and final sequences in `src/main/java/com/cardozojavier/pocmcpserver/mcp/order/OrderRequestValidator.java`
- [ ] T028 [US3] Run `./mvnw test -Dtest=CreateOrderToolRecurringTests,CreateOrderToolValidationTests` for `src/test/java/com/cardozojavier/pocmcpserver/mcp/order/CreateOrderToolRecurringTests.java`

**Checkpoint**: All user stories are independently functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Complete manual verification, documentation synchronization, and regression checks across all stories.

- [ ] T029 [P] Add a `Build Order Request` MCP call example and response assertions in `postman/poc-mcp-server-tools.postman_collection.json`
- [ ] T030 [P] Update manual verification steps to mention the final Postman request name in `specs/003-create-order-tool/quickstart.md`
- [ ] T031 [P] Add repository text guard test or documented check for vendor-specific naming in `src/test/java/com/cardozojavier/pocmcpserver/mcp/order/CreateOrderToolContractTests.java`
- [ ] T032 Run full regression suite with `./mvnw test` for `pom.xml`
- [ ] T033 Run the repository naming guard for the prohibited vendor term from `specs/003-create-order-tool/quickstart.md`
- [ ] T034 Confirm every implemented behavior still matches `specs/003-create-order-tool/contracts/build-order-request.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies; can start immediately.
- **Foundational (Phase 2)**: Depends on Setup completion; blocks all user stories.
- **User Story 1 (Phase 3)**: Depends on Foundational completion; delivers MVP.
- **User Story 2 (Phase 4)**: Depends on Foundational completion and can be implemented after or alongside US1 once shared validation types exist.
- **User Story 3 (Phase 5)**: Depends on Foundational completion and can be implemented after or alongside US1 once shared validation types exist.
- **Polish (Phase 6)**: Depends on all desired user stories being complete.

### User Story Dependencies

- **US1 (P1)**: No dependency on other user stories; recommended MVP.
- **US2 (P2)**: Uses the same draft and validation response types as US1 but remains independently testable through authentication-specific tests.
- **US3 (P3)**: Uses the same draft and validation response types as US1 but remains independently testable through recurring-specific tests.

### Within Each User Story

- Tests must be written and observed failing before implementation.
- Validation model changes must precede validator behavior when a story needs new fields.
- Validator behavior must precede MCP response assertions.
- Story-specific test command must pass before moving to the next checkpoint.

---

## Parallel Opportunities

- T002 can run in parallel with T001 after the test package path exists.
- T004 and T005 can run in parallel after T003 begins because they are separate files.
- T007, T008, and T009 can run in parallel because they are separate test files.
- T016 and T017 can run in parallel because they add distinct authentication test scenarios in the same file only if coordinated; otherwise run sequentially.
- T022, T023, and T024 can run in parallel because they add distinct recurring test scenarios in the same file only if coordinated; otherwise run sequentially.
- T029, T030, and T031 can run in parallel after all user stories pass.

## Parallel Example: User Story 1

```bash
Task: "T007 Add MCP contract test in src/test/java/com/cardozojavier/pocmcpserver/mcp/order/CreateOrderToolContractTests.java"
Task: "T008 Add standard validation tests in src/test/java/com/cardozojavier/pocmcpserver/mcp/order/CreateOrderToolValidationTests.java"
Task: "T009 Add safe logging assertions in src/test/java/com/cardozojavier/pocmcpserver/mcp/PaymentGatewayToolsLoggingTests.java"
```

## Parallel Example: User Story 2

```bash
Task: "T016 Add authentication required tests in src/test/java/com/cardozojavier/pocmcpserver/mcp/order/CreateOrderToolAuthenticationTests.java"
Task: "T017 Add complete authentication warning tests in src/test/java/com/cardozojavier/pocmcpserver/mcp/order/CreateOrderToolAuthenticationTests.java"
```

## Parallel Example: User Story 3

```bash
Task: "T022 Add initial recurring tests in src/test/java/com/cardozojavier/pocmcpserver/mcp/order/CreateOrderToolRecurringTests.java"
Task: "T023 Add recurring and final sequence tests in src/test/java/com/cardozojavier/pocmcpserver/mcp/order/CreateOrderToolRecurringTests.java"
Task: "T024 Add invalid recurring rule tests in src/test/java/com/cardozojavier/pocmcpserver/mcp/order/CreateOrderToolRecurringTests.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 and Phase 2.
2. Write and confirm failing tests T007, T008, and T009.
3. Complete T010 through T015.
4. Stop and validate that standard request drafts work without submission.

### Incremental Delivery

1. Add US1 for standard request drafts and validate it independently.
2. Add US2 for authentication-enhanced requirements and re-run US1 tests.
3. Add US3 for recurring requirements and re-run US1 tests.
4. Complete Phase 6 for manual checks and cross-story regression.

### Parallel Team Strategy

1. One engineer completes Phase 1 and Phase 2.
2. Separate engineers can write US1, US2, and US3 tests in parallel.
3. Validator implementation tasks that touch `OrderRequestValidator.java` must be coordinated or sequenced to avoid conflicts.

## Notes

- [P] tasks are marked only when they touch different files or can be coordinated safely.
- Every user story phase starts with tests that must fail for missing behavior.
- The tool must remain validation-only through every task.
- Repository artifacts must retain neutral payment-gateway wording.
