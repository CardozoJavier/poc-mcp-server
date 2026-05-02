# MCP Tool Contract: `build_order_request`

## Purpose

Guide a merchant through building and validating an order creation request draft. The tool does not submit the order, does not call a payment service, and does not persist the draft.

## Tool Metadata

- **Name**: `build_order_request`
- **Description**: Build and validate an order creation request draft for standard, authentication-enhanced, and recurring payment flows without submitting it.

## Input

The tool accepts one request draft object with the following top-level fields.

### Required Standard Fields

- `money.amount`: Decimal amount greater than zero.
- `money.currency`: ISO 4217 currency code.
- `notificationUrl`: Transaction notification URL.
- `redirectUrl.success`: Successful payment redirect URL.
- `redirectUrl.fail`: Failed payment redirect URL.
- `userInformation.ip`: Public IPv4 address.
- `userInformation.email`: Contact email address.
- `userInformation.country`: ISO 3166 country code.
- `userInformation.language`: ISO 639-1 language code.
- `userInformation.customerId`: Customer identifier.
- `userInformation.browserInformation.userAgentHeader`: Browser user-agent header.
- `userInformation.browserInformation.acceptHeader`: Browser accept header.
- `userInformation.browserInformation.fingerprint`: Browser fingerprint.
- `userInformation.domain`: Merchant domain URL.

### Optional Fields

- `purchaseId`: Merchant transaction identifier.
- `description`: Order description.
- `concept`: Order concept.
- `items[]`: Product details with `name`, `sku`, `quantity`, `unitPrice`, and `url`.
- `customTag`: Merchant tracking value.
- `processorContext`: Merchant-supplied indicators for processor-specific requirements.

### Authentication-Enhanced Fields

Required when the request context indicates enhanced authentication is required.

- `redirectUrl.threeDSChallengeResult`: Challenge completion redirect URL.
- `authentication.threeDSSupport`: Authentication support indicator.
- `authentication.cardHolderAccountInformation`: Account age and activity signals.
- `authentication.merchantRiskIndicator`: Risk and delivery indicators.
- `authentication.threeDSRequestorAuthenticationInfo`: Authentication method and timestamp.
- `authentication.deviceRenderingOptionsSupported`: Device rendering support details.

### Recurring Fields

Required when building a recurring request.

- `recurring.type`: Must be `recurrence`.
- `recurring.sequence`: Must be `initial`, `recurring`, or `final`.
- `recurring.minPeriod`: Integer number of days, minimum `1`.
- `recurring.generateToken`: Required true for initial setup when a future-use token is needed.
- `recurring.parentTransactionId`: Required for `recurring` and `final` sequences.

## Output

The tool returns a structured validation response.

- `ready`: Boolean. True only when the draft has no blocking validation issues.
- `flowTypes`: Array of detected flows, such as `standard`, `authentication_enhanced`, and `recurring`.
- `requestDraft`: Normalized draft object that the merchant can submit outside this tool.
- `missingFields`: Array of missing required field paths.
- `validationIssues`: Array of objects with `field`, `category`, and `message`.
- `warnings`: Array of non-blocking guidance messages.
- `nextSteps`: Array of merchant-readable next actions.

## Validation Categories

- `malformed_request`
- `schema_validation_failed`
- `invalid_currency`
- `invalid_country`
- `invalid_language`
- `invalid_ip_address`
- `invalid_url`
- `duplicate_purchase_identifier`
- `invalid_recurring_reference`
- `invalid_recurring_sequence`
- `invalid_amount`
- `unexpected_service_failure`

## Required Behavior

- The tool must not submit orders.
- The tool must not call a payment service.
- The tool must not report an order identifier.
- The tool must not claim that an order was created.
- The tool must keep customer and browser values out of logs.
- The tool must use neutral payment-gateway wording in tool metadata and responses.

## Example: Valid Standard Draft

```json
{
  "money": {
    "amount": 25.5,
    "currency": "USD"
  },
  "notificationUrl": "https://merchant.example/notifications",
  "redirectUrl": {
    "success": "https://merchant.example/success",
    "fail": "https://merchant.example/fail"
  },
  "userInformation": {
    "ip": "93.184.216.34",
    "email": "buyer@example.com",
    "country": "US",
    "language": "en",
    "customerId": "customer-123",
    "browserInformation": {
      "userAgentHeader": "Mozilla/5.0",
      "acceptHeader": "text/html,application/xhtml+xml",
      "fingerprint": "browser-fingerprint"
    },
    "domain": "https://merchant.example"
  }
}
```

## Example: Validation Response With Missing Fields

```json
{
  "ready": false,
  "flowTypes": ["standard"],
  "requestDraft": {},
  "missingFields": [
    "money.currency",
    "redirectUrl.success",
    "userInformation.ip"
  ],
  "validationIssues": [],
  "warnings": [],
  "nextSteps": [
    "Provide all missing standard fields before submitting the draft outside this tool."
  ]
}
```
