package com.cardozojavier.pocmcpserver.mcp.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateOrderToolValidationTests {

	private final OrderRequestValidator validator = new OrderRequestValidator();

	@Test
	void marksCompleteStandardDraftReadyWithoutSubmittingOrder() {
		ValidationResult result = validator.validate(OrderDraftTestFixtures.validStandardDraft());

		assertThat(result.ready()).isTrue();
		assertThat(result.flowTypes()).containsExactly("standard");
		assertThat(result.missingFields()).isEmpty();
		assertThat(result.validationIssues()).isEmpty();
		assertThat(result.requestDraft()).containsKeys(
				"money",
				"notificationUrl",
				"redirectUrl",
				"userInformation",
				"description",
				"concept",
				"items",
				"customTag");
		assertThat(result.nextSteps()).contains("Submit the validated request draft outside this tool.");
		assertThat(result.nextSteps()).noneMatch(step -> step.toLowerCase().contains("created"));
	}

	@Test
	void reportsAllMissingStandardFieldsInOneResponse() {
		ValidationResult result = validator.validate(Map.of());

		assertThat(result.ready()).isFalse();
		assertThat(result.missingFields()).contains(
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
	}

	@Test
	void rejectsInvalidCurrencyCountryLanguageIpAndUrls() {
		Map<String, Object> draft = OrderDraftTestFixtures.validStandardDraft();
		@SuppressWarnings("unchecked")
		Map<String, Object> money = (Map<String, Object>) draft.get("money");
		money.put("currency", "US");
		@SuppressWarnings("unchecked")
		Map<String, Object> redirectUrl = (Map<String, Object>) draft.get("redirectUrl");
		redirectUrl.put("success", "not-a-url");
		@SuppressWarnings("unchecked")
		Map<String, Object> userInformation = (Map<String, Object>) draft.get("userInformation");
		userInformation.put("ip", "192.168.1.10");
		userInformation.put("country", "USA");
		userInformation.put("language", "eng");
		userInformation.put("domain", "merchant.example");

		ValidationResult result = validator.validate(draft);

		assertThat(result.ready()).isFalse();
		assertThat(issueCategories(result)).contains(
				"invalid_currency",
				"invalid_country",
				"invalid_language",
				"invalid_ip_address",
				"invalid_url");
	}

	@Test
	void preservesOptionalOrderContextFields() {
		ValidationResult result = validator.validate(OrderDraftTestFixtures.validStandardDraft());

		assertThat(result.ready()).isTrue();
		assertThat(result.requestDraft()).containsEntry("description", "Order description");
		assertThat(result.requestDraft()).containsEntry("concept", "Order concept");
		assertThat(result.requestDraft()).containsEntry("customTag", "merchant-tracking-value");
		assertThat(result.requestDraft()).containsKey("items");
	}

	private static List<String> issueCategories(ValidationResult result) {
		List<String> categories = new ArrayList<>();
		for (ValidationIssue issue : result.validationIssues()) {
			categories.add(issue.category());
		}
		return categories;
	}

}
