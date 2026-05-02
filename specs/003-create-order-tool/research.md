# Research: Create Order Tool

## Decision: Implement a validation-only MCP tool

**Rationale**: The feature scope is to guide, build, and validate an order request draft. Avoiding service calls makes the tool safe for merchants during preparation and satisfies the requirement that it must not submit orders or imply creation.

**Alternatives considered**: A submission tool was rejected because it changes merchant risk, requires payment-service authentication, and is outside the clarified scope. A two-step submit flow was rejected for the same reason.

## Decision: Expose one MCP tool named `build_order_request`

**Rationale**: The current repository exposes MCP tools with snake_case names. A single tool can accept a draft with conditional sections and return readiness plus guidance without splitting state across multiple calls.

**Alternatives considered**: Separate tools for standard, authentication-enhanced, and recurring flows would duplicate shared validation and make merchant guidance harder to keep consistent.

## Decision: Keep validation stateless

**Rationale**: The tool prepares a request draft and reports issues. It does not need persistence, background jobs, or order lifecycle state because it does not create or track orders.

**Alternatives considered**: Persisting drafts was rejected because the spec does not require retrieval, continuation, or audit history, and storing customer/browser data would increase privacy scope.

## Decision: Validate documented standards locally

**Rationale**: Currency, country, language, public IPv4, URL, recurring sequence, token-generation, and parent-transaction rules can be evaluated before submission. Local validation gives merchants immediate feedback.

**Alternatives considered**: Deferring validation to the downstream order service was rejected because the feature exists to prevent avoidable request failures before submission.

## Decision: Treat duplicate purchase identifiers as a best-effort warning

**Rationale**: The spec requires duplicate-risk reporting when known to the tool, but this validation-only feature has no order lookup or persistence. The tool can validate presence/shape and include a duplicate-risk category only when duplicate information is supplied by the caller or a future dependency.

**Alternatives considered**: Calling an order lookup service was rejected because it would add external integration and authentication outside the first feature scope.

## Decision: Keep sensitive values out of logs

**Rationale**: Request drafts may contain user email, IP address, browser data, customer identifiers, and redirect URLs. Logs should include tool name, readiness, issue counts, and high-level flow type only.

**Alternatives considered**: Logging complete drafts was rejected because it would expose unnecessary customer and browser data.
