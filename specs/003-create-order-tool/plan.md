# Implementation Plan: Create Order Tool

**Branch**: `003-create-order-tool` | **Date**: 2026-05-02 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/003-create-order-tool/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Add the first real merchant-facing MCP tool for order creation preparation. The tool guides merchants through standard, authentication-enhanced, and recurring order request data, validates the request draft, and returns readiness, issues, and a neutral request draft without submitting an order or claiming that an order exists.

## Technical Context

**Language/Version**: Java 21  
**Primary Dependencies**: Spring Boot 4.0.6, Spring AI MCP Server WebMVC, Spring Security, Spring Actuator  
**Storage**: N/A; validation is stateless and does not persist order drafts  
**Testing**: Maven wrapper with JUnit 5, AssertJ, Spring Boot Test, Spring Security Test  
**Target Platform**: Spring Boot server exposing MCP over Streamable HTTP on the JVM  
**Project Type**: Single web service / MCP server  
**Performance Goals**: Validation-only tool response should complete within 1 second for normal merchant request drafts  
**Constraints**: Must not submit orders, must not call the payment service, must not persist sensitive customer or browser data, and must not include vendor-specific names in repository artifacts  
**Scale/Scope**: One new MCP tool contract covering standard, authentication-enhanced, and recurring order request draft validation

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- `Spec-Driven Planning`: PASS. Active spec is `specs/003-create-order-tool/spec.md`; plan is `specs/003-create-order-tool/plan.md`; implementation targets the existing Spring MCP server under `src/main/java/com/cardozojavier/pocmcpserver`.
- `Test-First Delivery`: PASS. Implementation tasks must first add failing tests for standard validation, authentication-enhanced validation, recurring validation, contract exposure, and safe logging before production code changes.
- `Contract-First Interfaces`: PASS. The new external interface is the MCP tool `build_order_request`, documented in `specs/003-create-order-tool/contracts/build-order-request.md`.
- `Minimal, Composable Changes`: PASS. Keep the feature inside the existing MCP component pattern, adding only focused order request model and validation code where complexity requires it. No new runtime dependencies are planned.
- `Scriptable, Traceable Operations`: PASS. Existing commands remain `./mvnw test` for verification and the Postman collection for manual MCP checks; `AGENTS.md` is updated to point at this plan.

## Project Structure

### Documentation (this feature)

```text
specs/003-create-order-tool/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── build-order-request.md
├── checklists/
│   └── requirements.md
└── spec.md
```

### Source Code (repository root)

```text
src/main/java/com/cardozojavier/pocmcpserver/
├── PocMcpServerApplication.java
├── config/
├── logging/
└── mcp/
    ├── PaymentGatewayTools.java
    └── order/
        ├── OrderRequestDraft.java
        ├── OrderRequestValidator.java
        └── validation result/value types

src/test/java/com/cardozojavier/pocmcpserver/
├── config/
├── logging/
└── mcp/
    ├── PaymentGatewayToolsLoggingTests.java
    └── order/
        ├── CreateOrderToolValidationTests.java
        ├── CreateOrderToolAuthenticationTests.java
        ├── CreateOrderToolRecurringTests.java
        └── CreateOrderToolContractTests.java

postman/
└── poc-mcp-server-tools.postman_collection.json
```

**Structure Decision**: Use the existing single-project Spring layout. Place reusable order draft and validation code under `mcp/order` so the current MCP tool component remains readable while preserving the repository's current package boundaries.

## Complexity Tracking

No constitution violations require complexity exceptions.

## Phase 0: Research

See [research.md](./research.md). All planning unknowns are resolved.

## Phase 1: Design & Contracts

See [data-model.md](./data-model.md), [contracts/build-order-request.md](./contracts/build-order-request.md), and [quickstart.md](./quickstart.md).

## Post-Design Constitution Check

- `Spec-Driven Planning`: PASS. Design artifacts are present under `specs/003-create-order-tool/`.
- `Test-First Delivery`: PASS. Quickstart and data model identify failing-test-first coverage before production code.
- `Contract-First Interfaces`: PASS. MCP tool name, input groups, output fields, and validation semantics are documented before implementation.
- `Minimal, Composable Changes`: PASS. No new dependency or service boundary is introduced; new order classes are scoped to request draft validation.
- `Scriptable, Traceable Operations`: PASS. Verification remains scriptable with `./mvnw test`; manual MCP verification is documented through Postman.
