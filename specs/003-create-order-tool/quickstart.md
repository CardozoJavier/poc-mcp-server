# Quickstart: Create Order Tool

## Prerequisites

- Java 21 is available.
- The Maven wrapper is executable.
- The active feature is `003-create-order-tool`.

## Test-First Implementation Flow

1. Add failing tests for standard request draft validation:

   ```bash
   ./mvnw test -Dtest=CreateOrderToolValidationTests
   ```

2. Add failing tests for authentication-enhanced validation:

   ```bash
   ./mvnw test -Dtest=CreateOrderToolAuthenticationTests
   ```

3. Add failing tests for recurring validation:

   ```bash
   ./mvnw test -Dtest=CreateOrderToolRecurringTests
   ```

4. Add failing tests for MCP contract exposure and safe logging:

   ```bash
   ./mvnw test -Dtest=CreateOrderToolContractTests,PaymentGatewayToolsLoggingTests
   ```

5. Implement the tool and validation logic until all tests pass:

   ```bash
   ./mvnw test
   ```

## Manual MCP Check

1. Start the application:

   ```bash
   ./mvnw spring-boot:run
   ```

2. Import `postman/poc-mcp-server-tools.postman_collection.json` into Postman.

3. Run these requests in order:

   - `Health`
   - `Initialize Session`
   - `Initialized Notification`
   - `List Tools`
   - New `Build Order Request` request added during implementation

4. Confirm the tool response includes:

   - `ready`
   - `flowTypes`
   - `requestDraft`
   - `missingFields`
   - `validationIssues`
   - `warnings`
   - `nextSteps`

## Acceptance Checks

- A complete standard draft is marked ready without submitting an order.
- Missing required fields are reported in one response.
- Authentication-enhanced requirements are enforced when the order context requires them.
- Recurring sequence rules are enforced for initial, recurring, and final flows.
- Logs do not include customer email, IP address, browser fingerprint, redirect URLs, or complete request drafts.
