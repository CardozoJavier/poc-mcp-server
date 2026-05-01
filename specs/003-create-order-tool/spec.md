# Feature Specification: Create Order Tool

**Feature Branch**: `003-create-order-tool`  
**Created**: 2026-05-02  
**Status**: Draft  
**Input**: User description: "Create a merchant-guided MCP tool for building and validating order creation requests using the existing payment gateway order reference as the source of truth. The tool must not submit orders. It must cover standard, authentication-enhanced, and recurring order flows."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Build a Standard Order Request (Priority: P1)

As a merchant, I want guided prompts for the required standard order fields so that I can produce a complete order creation request without needing to know the full request structure in advance.

**Why this priority**: A complete standard order request is the minimum useful outcome and the base for every advanced flow.

**Independent Test**: Can be fully tested by providing valid standard order inputs and confirming the tool returns a complete, non-submitted request draft with no missing required fields.

**Acceptance Scenarios**:

1. **Given** a merchant has amount, currency, notification URL, redirect URLs, user information, browser information, and merchant domain, **When** they ask to create an order request, **Then** the tool returns a structured draft marked ready for submission outside the tool.
2. **Given** a merchant omits a required standard field, **When** they ask to create an order request, **Then** the tool identifies the missing field, explains why it is needed, and does not mark the draft ready.
3. **Given** a merchant provides optional description, concept, item, or tracking details, **When** they ask to create an order request, **Then** the tool includes those details in the draft without requiring them for readiness.

**Test-First Proof**: `CreateOrderToolValidationTests` covers complete standard requests, missing required fields, and optional context fields before implementation.

---

### User Story 2 - Validate Authentication-Enhanced Requirements (Priority: P2)

As a merchant, I want the tool to recognize when authentication-enhanced payment data is needed so that regulated or higher-risk orders include the correct redirect and authentication context before submission.

**Why this priority**: Some regions require additional authentication support, and missing those fields can prevent a merchant from completing payment setup.

**Independent Test**: Can be tested by providing an order for a region requiring enhanced authentication and confirming the tool requires authentication support details and the additional challenge redirect URL.

**Acceptance Scenarios**:

1. **Given** an order involves a region where enhanced authentication is required, **When** the merchant builds the request, **Then** the tool requires authentication support data and a challenge-result redirect URL before marking the draft ready.
2. **Given** a merchant supplies authentication risk and requestor details, **When** the tool validates the request, **Then** it preserves those details in the draft and reports the authentication section as complete.
3. **Given** authentication fields are included for an order where authentication may not ultimately occur, **When** the tool validates the request, **Then** it explains that inclusion supports the process but does not guarantee an authentication challenge.

**Test-First Proof**: `CreateOrderToolAuthenticationTests` covers region-triggered requirements, complete authentication details, and explanatory guidance for optional challenge behavior before implementation.

---

### User Story 3 - Build Recurring Order Requests (Priority: P3)

As a merchant, I want the tool to guide initial, recurring, and final recurring order stages so that every recurring request has the correct sequence, token, period, and parent transaction relationship.

**Why this priority**: Recurring flows add conditional rules that are easy to misapply and can cause preventable request failures.

**Independent Test**: Can be tested by validating one initial recurrence, one recurring sequence, and one final sequence with the correct conditional fields for each stage.

**Acceptance Scenarios**:

1. **Given** a merchant starts an initial recurring setup, **When** they build the request, **Then** the tool requires recurring payment details, a valid minimum period, and token generation.
2. **Given** a merchant builds a recurring or final sequence, **When** they validate the request, **Then** the tool requires a parent transaction reference and prevents token generation for that sequence.
3. **Given** a merchant provides a minimum period below the allowed minimum, **When** they validate the request, **Then** the tool reports the minimum-period violation and does not mark the draft ready.

**Test-First Proof**: `CreateOrderToolRecurringTests` covers initial, recurring, final, missing parent reference, token-generation rules, and minimum-period validation before implementation.

### Edge Cases

- Invalid currency, country, or language codes must be reported with the affected field and accepted standard.
- Private, malformed, or unsupported IP addresses must prevent readiness.
- A merchant-provided purchase identifier that is already associated with a pending or successful order must be reported as a duplicate-risk validation result when known to the tool.
- Malformed notification, success, fail, or challenge-result URLs must prevent readiness.
- Processor-specific details such as billing country or merchant city must be requested when the merchant indicates the corresponding connection or descriptor setup applies.
- The tool must not submit the order or claim that an order exists; it only returns a validated request draft and guidance.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The tool MUST guide merchants through standard order request creation without submitting the order.
- **FR-002**: The tool MUST require standard order data: amount, currency, notification URL, success and fail redirect URLs, public user IP address, user email, user country, user language, customer identifier, browser user-agent header, browser accept header, browser fingerprint, and merchant domain.
- **FR-003**: The tool MUST support optional order context fields including description, concept, item details, and merchant tracking tag.
- **FR-004**: The tool MUST validate currency as ISO 4217, country as ISO 3166, language as ISO 639-1, and user IP address as a valid public IPv4 address.
- **FR-005**: The tool MUST validate that required URL fields are present and syntactically valid before marking a draft ready.
- **FR-006**: The tool MUST support authentication-enhanced order guidance, including authentication support indication, challenge-result redirect URL, and authentication detail sections for account, risk, requestor, and rendering context.
- **FR-007**: The tool MUST require authentication-enhanced fields when the order context indicates a region where enhanced authentication is required.
- **FR-008**: The tool MUST explain that providing authentication-enhanced fields supports the process but does not guarantee an authentication challenge.
- **FR-009**: The tool MUST support recurring order guidance for initial, recurring, and final sequences.
- **FR-010**: The tool MUST require recurring payment details with type, sequence, and minimum period for recurring order flows.
- **FR-011**: The tool MUST require token generation for an initial recurring setup when a future-use token is needed.
- **FR-012**: The tool MUST require a parent transaction reference for recurring and final sequences and MUST prevent those sequences from generating a new token.
- **FR-013**: The tool MUST validate recurring minimum period as at least 1 day.
- **FR-014**: The tool MUST surface known validation failures using merchant-readable categories for malformed request, schema validation failure, invalid currency, invalid country, invalid language, invalid IP address, duplicate purchase identifier, invalid recurring reference, and unexpected service failure.
- **FR-015**: The tool MUST return a structured request draft, readiness status, missing fields, validation issues, and next-step guidance.
- **FR-016**: The tool MUST define the external contract for the new MCP tool, including input fields, conditional groups, validation results, and output shape, before implementation.
- **FR-017**: The tool MUST avoid vendor-specific names in repository artifacts, tool names, descriptions, contracts, tests, logs, and documentation.

### Key Entities

- **Order Request Draft**: A merchant-built request that contains payment amount, currency, redirects, notifications, user information, browser information, domain, optional context, and conditional sections.
- **Validation Result**: The readiness state for the draft, including missing required fields, invalid fields, duplicate-risk indicators, and merchant-readable recovery guidance.
- **Authentication Details**: Conditional data used to support enhanced authentication, including challenge redirect, account information, risk indicators, requestor authentication information, and rendering options.
- **Recurring Details**: Conditional data describing recurrence type, sequence, minimum period, token-generation intent, and parent transaction relationship.
- **Item Detail**: Optional product-level data such as name, SKU, quantity, unit price, and URL.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Merchants can produce a complete standard order request draft in under 5 minutes when they have the required data available.
- **SC-002**: At least 95% of incomplete request attempts identify all missing required fields in a single validation response.
- **SC-003**: At least 95% of invalid code, URL, IP address, and recurring-rule inputs are rejected before a draft is marked ready.
- **SC-004**: Merchants can distinguish standard, authentication-enhanced, and recurring requirements without consulting external reference material during the guided flow.
- **SC-005**: No successful tool response implies an order was submitted or created.

## Assumptions

- Merchants already have access to the required customer, browser, redirect, notification, and domain information before using the tool.
- The first version prepares and validates order requests only; submission to the payment service is out of scope.
- Duplicate purchase identifier detection is limited to information available to the tool at validation time.
- Processor-specific fields are requested only when the merchant identifies that the relevant connection or descriptor setup applies.
- Repository artifacts must use neutral payment-gateway language and must not include vendor-specific names.
