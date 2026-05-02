package com.cardozojavier.pocmcpserver.mcp.order;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public final class OrderDraftTestFixtures {

	private OrderDraftTestFixtures() {
	}

	public static Map<String, Object> validStandardDraft() {
		Map<String, Object> draft = new LinkedHashMap<>();
		draft.put("money", money("25.50", "USD"));
		draft.put("notificationUrl", "https://merchant.example/notifications");
		draft.put("redirectUrl", redirectUrls());
		draft.put("userInformation", userInformation());
		draft.put("description", "Order description");
		draft.put("concept", "Order concept");
		draft.put("customTag", "merchant-tracking-value");
		draft.put("items", new Object[] {
				item("T-Shirt", "sku-123", 2, "12.75", "https://merchant.example/items/sku-123")
		});
		return draft;
	}

	public static Map<String, Object> validAuthenticationDraft() {
		Map<String, Object> draft = validStandardDraft();
		draft.put("requiresAuthentication", true);
		@SuppressWarnings("unchecked")
		Map<String, Object> redirectUrl = (Map<String, Object>) draft.get("redirectUrl");
		redirectUrl.put("threeDSChallengeResult", "https://merchant.example/challenge-result");
		draft.put("authentication", authenticationDetails());
		return draft;
	}

	public static Map<String, Object> validInitialRecurringDraft() {
		Map<String, Object> draft = validStandardDraft();
		draft.put("recurring", recurring("recurrence", "initial", 30, true, null));
		return draft;
	}

	public static Map<String, Object> validRecurringDraft() {
		Map<String, Object> draft = validStandardDraft();
		draft.put("recurring", recurring("recurrence", "recurring", 30, false, "txn-initial-123"));
		return draft;
	}

	public static Map<String, Object> validFinalRecurringDraft() {
		Map<String, Object> draft = validStandardDraft();
		draft.put("recurring", recurring("recurrence", "final", 30, false, "txn-initial-123"));
		return draft;
	}

	static Map<String, Object> money(String amount, String currency) {
		Map<String, Object> money = new LinkedHashMap<>();
		money.put("amount", new BigDecimal(amount));
		money.put("currency", currency);
		return money;
	}

	static Map<String, Object> redirectUrls() {
		Map<String, Object> redirectUrl = new LinkedHashMap<>();
		redirectUrl.put("success", "https://merchant.example/success");
		redirectUrl.put("fail", "https://merchant.example/fail");
		return redirectUrl;
	}

	static Map<String, Object> userInformation() {
		Map<String, Object> userInformation = new LinkedHashMap<>();
		userInformation.put("ip", "93.184.216.34");
		userInformation.put("email", "buyer@example.com");
		userInformation.put("country", "US");
		userInformation.put("language", "en");
		userInformation.put("customerId", "customer-123");
		userInformation.put("browserInformation", browserInformation());
		userInformation.put("domain", "https://merchant.example");
		return userInformation;
	}

	static Map<String, Object> browserInformation() {
		Map<String, Object> browserInformation = new LinkedHashMap<>();
		browserInformation.put("userAgentHeader", "Mozilla/5.0");
		browserInformation.put("acceptHeader", "text/html,application/xhtml+xml");
		browserInformation.put("fingerprint", "browser-fingerprint");
		return browserInformation;
	}

	static Map<String, Object> authenticationDetails() {
		Map<String, Object> authentication = new LinkedHashMap<>();
		authentication.put("threeDSSupport", true);
		authentication.put("cardHolderAccountInformation", Map.of("accountAgeIndicator", "more_than_60_days"));
		authentication.put("merchantRiskIndicator", Map.of("deliveryEmail", "buyer@example.com"));
		authentication.put("threeDSRequestorAuthenticationInfo", Map.of("method", "merchant_credentials"));
		authentication.put("deviceRenderingOptionsSupported", Map.of("sdkInterface", "browser"));
		return authentication;
	}

	static Map<String, Object> recurring(
			String type,
			String sequence,
			int minPeriod,
			boolean generateToken,
			String parentTransactionId) {

		Map<String, Object> recurring = new LinkedHashMap<>();
		recurring.put("type", type);
		recurring.put("sequence", sequence);
		recurring.put("minPeriod", minPeriod);
		recurring.put("generateToken", generateToken);
		if (parentTransactionId != null) {
			recurring.put("parentTransactionId", parentTransactionId);
		}
		return recurring;
	}

	private static Map<String, Object> item(String name, String sku, int quantity, String unitPrice, String url) {
		Map<String, Object> item = new LinkedHashMap<>();
		item.put("name", name);
		item.put("sku", sku);
		item.put("quantity", quantity);
		item.put("unitPrice", new BigDecimal(unitPrice));
		item.put("url", url);
		return item;
	}

}
