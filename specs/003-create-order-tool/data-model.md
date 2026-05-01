# Data Model: Create Order Tool

## OrderRequestDraft

Represents the merchant-prepared order request before submission.

**Fields**

- `money`: Required money amount and currency.
- `notificationUrl`: Required transaction notification URL.
- `redirectUrl`: Required success and fail URLs; conditionally includes challenge-result URL.
- `userInformation`: Required user, customer, browser, and domain context.
- `purchaseId`: Optional merchant-provided transaction identifier; must be unique when known.
- `description`: Optional order description.
- `concept`: Optional order concept.
- `items`: Optional product line items.
- `customTag`: Optional merchant tracking tag.
- `authentication`: Conditional authentication-enhanced details.
- `recurring`: Conditional recurring payment details.
- `processorContext`: Optional processor-specific indicators used to request billing country or merchant city when applicable.

**Validation**

- Required standard fields must be present before readiness.
- Currency must be ISO 4217.
- Country must be ISO 3166.
- Language must be ISO 639-1.
- User IP must be public IPv4.
- URLs must be syntactically valid.
- The draft is never submitted by this feature.

## Money

**Fields**

- `amount`: Required decimal amount.
- `currency`: Required ISO 4217 code.

**Validation**

- Amount must be present and greater than zero.
- Currency must normalize to uppercase ISO 4217.

## RedirectUrl

**Fields**

- `success`: Required URL for successful payment redirection.
- `fail`: Required URL for failed payment redirection.
- `threeDSChallengeResult`: Conditional URL for authentication challenge completion.

**Validation**

- `success` and `fail` are always required.
- `threeDSChallengeResult` is required when authentication-enhanced fields are required.

## UserInformation

**Fields**

- `ip`: Required public IPv4 address.
- `email`: Required contact email.
- `country`: Required ISO 3166 country code.
- `language`: Required ISO 639-1 language code.
- `customerId`: Required customer identifier.
- `browserInformation`: Required browser context.
- `domain`: Required merchant domain URL.

**Validation**

- IP must not be private, loopback, malformed, or unsupported.
- Email must be syntactically valid enough to catch malformed addresses.
- Country and language must match the documented standards.
- Domain must be a syntactically valid URL.

## BrowserInformation

**Fields**

- `userAgentHeader`: Required browser user-agent header.
- `acceptHeader`: Required browser accept header.
- `fingerprint`: Required browser fingerprint value.

**Validation**

- All fields must be non-blank for readiness.

## AuthenticationDetails

**Fields**

- `threeDSSupport`: Indicates support for authentication-enhanced processing.
- `cardHolderAccountInformation`: Account age and activity signals.
- `merchantRiskIndicator`: Risk and delivery indicators.
- `threeDSRequestorAuthenticationInfo`: Requestor authentication method and timestamp.
- `deviceRenderingOptionsSupported`: Device rendering support details.

**Validation**

- Required when the order context indicates a region where enhanced authentication is required.
- Providing these fields supports authentication but must not be described as guaranteeing a challenge.

## RecurringDetails

**Fields**

- `type`: Required for recurring flows; expected value is `recurrence`.
- `sequence`: Required recurring stage: `initial`, `recurring`, or `final`.
- `minPeriod`: Required minimum days between payments.
- `generateToken`: Required true for initial setup when a future-use token is needed.
- `parentTransactionId`: Required for `recurring` and `final` sequences.

**Validation**

- `minPeriod` must be at least 1.
- `recurring` and `final` sequences must include `parentTransactionId`.
- `recurring` and `final` sequences must not generate a new token.

## ItemDetail

**Fields**

- `name`: Product name.
- `sku`: Product SKU.
- `quantity`: Product quantity.
- `unitPrice`: Product unit price.
- `url`: Product URL.

**Validation**

- Item details are optional, but provided quantities and prices must be valid positive values.
- Provided URLs must be syntactically valid.

## ValidationResult

Represents the tool response.

**Fields**

- `ready`: True only when no blocking validation issues remain.
- `flowTypes`: Reported flow coverage, such as `standard`, `authentication_enhanced`, and `recurring`.
- `requestDraft`: Normalized request draft.
- `missingFields`: Required fields not supplied.
- `validationIssues`: Invalid or inconsistent fields.
- `warnings`: Non-blocking guidance, such as duplicate-risk uncertainty or authentication challenge uncertainty.
- `nextSteps`: Merchant-readable actions to complete or use the draft outside the tool.

**Validation**

- `ready` must be false whenever missing fields or blocking validation issues exist.
- Successful responses must not imply order submission or creation.
