package com.cardozojavier.pocmcpserver.mcp.order;

import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class OrderRequestValidator {

	private static final Set<String> ISO_COUNTRIES = Set.of(Locale.getISOCountries());

	private static final Set<String> ISO_LANGUAGES = Set.of(Locale.getISOLanguages());

	private static final List<String> REQUIRED_STANDARD_FIELDS = List.of(
			"money.amount",
			"money.currency",
			"notificationUrl",
			"redirectUrl.success",
			"redirectUrl.fail",
			"userInformation.ip",
			"userInformation.email",
			"userInformation.country",
			"userInformation.language",
			"userInformation.customerId",
			"userInformation.browserInformation.userAgentHeader",
			"userInformation.browserInformation.acceptHeader",
			"userInformation.browserInformation.fingerprint",
			"userInformation.domain");

	public ValidationResult validate(Map<String, Object> draft) {
		Map<String, Object> source = draft == null ? Map.of() : draft;
		List<String> missingFields = new ArrayList<>();
		List<ValidationIssue> issues = new ArrayList<>();

		for (String field : REQUIRED_STANDARD_FIELDS) {
			if (isBlank(valueAt(source, field))) {
				missingFields.add(field);
			}
		}

		validateAmount(source, issues);
		validateCurrency(source, issues);
		validateCountry(source, issues);
		validateLanguage(source, issues);
		validatePublicIpv4(source, issues);
		validateUrl(source, "notificationUrl", issues);
		validateUrl(source, "redirectUrl.success", issues);
		validateUrl(source, "redirectUrl.fail", issues);
		validateUrl(source, "userInformation.domain", issues);
		validateEmail(source, issues);

		List<String> flowTypes = new ArrayList<>();
		flowTypes.add("standard");
		if (isAuthenticationFlow(source)) {
			flowTypes.add("authentication_enhanced");
			requireAuthenticationFields(source, missingFields);
			validateUrl(source, "redirectUrl.threeDSChallengeResult", issues);
		}
		if (isRecurringFlow(source)) {
			flowTypes.add("recurring");
			requireRecurringFields(source, missingFields, issues);
			validateRecurringRules(source, missingFields, issues);
		}

		boolean ready = missingFields.isEmpty() && issues.isEmpty();
		Map<String, Object> normalizedDraft = ready ? normalizeStandardDraft(source) : new LinkedHashMap<>();
		List<String> warnings = isAuthenticationFlow(source)
				? List.of("Authentication fields support the process but do not guarantee a challenge.")
				: List.of();
		List<String> nextSteps = ready
				? List.of("Submit the validated request draft outside this tool.")
				: List.of("Provide all missing or invalid standard fields before submitting the draft outside this tool.");

		return new ValidationResult(
				ready,
				List.copyOf(flowTypes),
				normalizedDraft,
				List.copyOf(missingFields),
				List.copyOf(issues),
				warnings,
				nextSteps);
	}

	private static boolean isAuthenticationFlow(Map<String, Object> source) {
		return Boolean.TRUE.equals(valueAt(source, "requiresAuthentication")) || valueAt(source, "authentication") != null;
	}

	private static void requireAuthenticationFields(Map<String, Object> source, List<String> missingFields) {
		List<String> requiredFields = List.of(
				"redirectUrl.threeDSChallengeResult",
				"authentication.threeDSSupport",
				"authentication.cardHolderAccountInformation",
				"authentication.merchantRiskIndicator",
				"authentication.threeDSRequestorAuthenticationInfo",
				"authentication.deviceRenderingOptionsSupported");
		for (String field : requiredFields) {
			if (isBlank(valueAt(source, field))) {
				missingFields.add(field);
			}
		}
	}

	private static boolean isRecurringFlow(Map<String, Object> source) {
		return valueAt(source, "recurring") != null;
	}

	private static void requireRecurringFields(
			Map<String, Object> source,
			List<String> missingFields,
			List<ValidationIssue> issues) {

		List<String> requiredFields = List.of("recurring.type", "recurring.sequence", "recurring.minPeriod");
		for (String field : requiredFields) {
			if (isBlank(valueAt(source, field))) {
				missingFields.add(field);
			}
		}

		String type = stringValue(valueAt(source, "recurring.type"));
		if (type != null && !"recurrence".equals(type)) {
			issues.add(new ValidationIssue("recurring.type", "invalid_recurring_sequence", "Recurring type must be recurrence."));
		}
	}

	private static void validateRecurringRules(
			Map<String, Object> source,
			List<String> missingFields,
			List<ValidationIssue> issues) {

		String sequence = stringValue(valueAt(source, "recurring.sequence"));
		Integer minPeriod = toInteger(valueAt(source, "recurring.minPeriod"));
		Boolean generateToken = toBoolean(valueAt(source, "recurring.generateToken"));
		String parentTransactionId = stringValue(valueAt(source, "recurring.parentTransactionId"));

		if (sequence == null) {
			return;
		}
		if (!List.of("initial", "recurring", "final").contains(sequence)) {
			issues.add(new ValidationIssue("recurring.sequence", "invalid_recurring_sequence", "Recurring sequence must be initial, recurring, or final."));
			return;
		}
		if (minPeriod == null || minPeriod < 1) {
			issues.add(new ValidationIssue("recurring.minPeriod", "invalid_recurring_sequence", "Recurring minimum period must be at least 1 day."));
		}
		if ("initial".equals(sequence) && !Boolean.TRUE.equals(generateToken)) {
			issues.add(new ValidationIssue("recurring.generateToken", "invalid_recurring_sequence", "Initial recurring setup must generate a token."));
		}
		if (List.of("recurring", "final").contains(sequence)) {
			if (parentTransactionId == null) {
				missingFields.add("recurring.parentTransactionId");
				issues.add(new ValidationIssue("recurring.parentTransactionId", "invalid_recurring_reference", "Recurring and final sequences require a parent transaction reference."));
			}
			if (Boolean.TRUE.equals(generateToken)) {
				issues.add(new ValidationIssue("recurring.generateToken", "invalid_recurring_sequence", "Recurring and final sequences must not generate a new token."));
			}
		}
	}

	private static void validateAmount(Map<String, Object> draft, List<ValidationIssue> issues) {
		Object amount = valueAt(draft, "money.amount");
		if (isBlank(amount)) {
			return;
		}
		BigDecimal decimalAmount = toBigDecimal(amount);
		if (decimalAmount == null || decimalAmount.compareTo(BigDecimal.ZERO) <= 0) {
			issues.add(new ValidationIssue("money.amount", "invalid_amount", "Amount must be greater than zero."));
		}
	}

	private static void validateCurrency(Map<String, Object> draft, List<ValidationIssue> issues) {
		String currency = stringValue(valueAt(draft, "money.currency"));
		if (currency == null) {
			return;
		}
		try {
			Currency.getInstance(currency.toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException ex) {
			issues.add(new ValidationIssue("money.currency", "invalid_currency", "Currency must be an ISO 4217 code."));
		}
	}

	private static void validateCountry(Map<String, Object> draft, List<ValidationIssue> issues) {
		String country = stringValue(valueAt(draft, "userInformation.country"));
		if (country != null && !ISO_COUNTRIES.contains(country.toUpperCase(Locale.ROOT))) {
			issues.add(new ValidationIssue("userInformation.country", "invalid_country", "Country must be an ISO 3166 code."));
		}
	}

	private static void validateLanguage(Map<String, Object> draft, List<ValidationIssue> issues) {
		String language = stringValue(valueAt(draft, "userInformation.language"));
		if (language != null && !ISO_LANGUAGES.contains(language.toLowerCase(Locale.ROOT))) {
			issues.add(new ValidationIssue("userInformation.language", "invalid_language", "Language must be an ISO 639-1 code."));
		}
	}

	private static void validatePublicIpv4(Map<String, Object> draft, List<ValidationIssue> issues) {
		String ip = stringValue(valueAt(draft, "userInformation.ip"));
		if (ip == null) {
			return;
		}
		try {
			InetAddress address = InetAddress.getByName(ip);
			if (!(address instanceof Inet4Address) || address.isAnyLocalAddress() || address.isLoopbackAddress()
					|| address.isLinkLocalAddress() || address.isSiteLocalAddress() || address.isMulticastAddress()) {
				issues.add(new ValidationIssue("userInformation.ip", "invalid_ip_address", "IP address must be public IPv4."));
			}
		}
		catch (Exception ex) {
			issues.add(new ValidationIssue("userInformation.ip", "invalid_ip_address", "IP address must be public IPv4."));
		}
	}

	private static void validateUrl(Map<String, Object> draft, String field, List<ValidationIssue> issues) {
		String url = stringValue(valueAt(draft, field));
		if (url == null) {
			return;
		}
		try {
			URI uri = URI.create(url);
			String scheme = uri.getScheme();
			if (uri.getHost() == null || (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))) {
				issues.add(new ValidationIssue(field, "invalid_url", "URL must include an HTTP or HTTPS scheme and host."));
			}
		}
		catch (IllegalArgumentException ex) {
			issues.add(new ValidationIssue(field, "invalid_url", "URL must include an HTTP or HTTPS scheme and host."));
		}
	}

	private static void validateEmail(Map<String, Object> draft, List<ValidationIssue> issues) {
		String email = stringValue(valueAt(draft, "userInformation.email"));
		if (email != null && (!email.contains("@") || email.startsWith("@") || email.endsWith("@"))) {
			issues.add(new ValidationIssue("userInformation.email", "schema_validation_failed", "Email must be syntactically valid."));
		}
	}

	private static Map<String, Object> normalizeStandardDraft(Map<String, Object> source) {
		Map<String, Object> normalized = new LinkedHashMap<>(source);
		@SuppressWarnings("unchecked")
		Map<String, Object> money = new LinkedHashMap<>((Map<String, Object>) source.get("money"));
		money.put("currency", stringValue(money.get("currency")).toUpperCase(Locale.ROOT));
		normalized.put("money", money);
		return normalized;
	}

	@SuppressWarnings("unchecked")
	private static Object valueAt(Map<String, Object> source, String path) {
		Object current = source;
		for (String segment : path.split("\\.")) {
			if (!(current instanceof Map<?, ?> currentMap)) {
				return null;
			}
			current = ((Map<String, Object>) currentMap).get(segment);
		}
		return current;
	}

	private static boolean isBlank(Object value) {
		if (value == null) {
			return true;
		}
		if (value instanceof String string) {
			return string.trim().isEmpty();
		}
		return false;
	}

	private static String stringValue(Object value) {
		if (value == null) {
			return null;
		}
		String string = value.toString().trim();
		return string.isEmpty() ? null : string;
	}

	private static BigDecimal toBigDecimal(Object value) {
		if (value instanceof BigDecimal decimal) {
			return decimal;
		}
		if (value instanceof Number number) {
			return new BigDecimal(number.toString());
		}
		try {
			return new BigDecimal(value.toString());
		}
		catch (NumberFormatException ex) {
			return null;
		}
	}

	private static Integer toInteger(Object value) {
		if (value instanceof Integer integer) {
			return integer;
		}
		if (value instanceof Number number) {
			return number.intValue();
		}
		if (value == null) {
			return null;
		}
		try {
			return Integer.parseInt(value.toString());
		}
		catch (NumberFormatException ex) {
			return null;
		}
	}

	private static Boolean toBoolean(Object value) {
		if (value instanceof Boolean bool) {
			return bool;
		}
		if (value == null) {
			return null;
		}
		return Boolean.parseBoolean(value.toString());
	}

}
