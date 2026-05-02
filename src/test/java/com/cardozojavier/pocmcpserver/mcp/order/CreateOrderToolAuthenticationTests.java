package com.cardozojavier.pocmcpserver.mcp.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateOrderToolAuthenticationTests {

	private final OrderRequestValidator validator = new OrderRequestValidator();

	@Test
	void requiresAuthenticationFieldsAndChallengeRedirectWhenContextRequiresAuthentication() {
		Map<String, Object> draft = OrderDraftTestFixtures.validStandardDraft();
		draft.put("requiresAuthentication", true);

		ValidationResult result = validator.validate(draft);

		assertThat(result.ready()).isFalse();
		assertThat(result.flowTypes()).containsExactly("standard", "authentication_enhanced");
		assertThat(result.missingFields()).contains(
				"redirectUrl.threeDSChallengeResult",
				"authentication.threeDSSupport",
				"authentication.cardHolderAccountInformation",
				"authentication.merchantRiskIndicator",
				"authentication.threeDSRequestorAuthenticationInfo",
				"authentication.deviceRenderingOptionsSupported");
	}

	@Test
	void preservesCompleteAuthenticationDetailsAndMarksDraftReady() {
		ValidationResult result = validator.validate(OrderDraftTestFixtures.validAuthenticationDraft());

		assertThat(result.ready()).isTrue();
		assertThat(result.flowTypes()).containsExactly("standard", "authentication_enhanced");
		assertThat(result.requestDraft()).containsKey("authentication");
		assertThat(result.warnings()).contains("Authentication fields support the process but do not guarantee a challenge.");
	}

	@Test
	void rejectsMalformedChallengeRedirect() {
		Map<String, Object> draft = OrderDraftTestFixtures.validAuthenticationDraft();
		@SuppressWarnings("unchecked")
		Map<String, Object> redirectUrl = (Map<String, Object>) draft.get("redirectUrl");
		redirectUrl.put("threeDSChallengeResult", "not-a-url");

		ValidationResult result = validator.validate(draft);

		assertThat(result.ready()).isFalse();
		assertThat(issueCategories(result)).contains("invalid_url");
	}

	private static List<String> issueCategories(ValidationResult result) {
		List<String> categories = new ArrayList<>();
		for (ValidationIssue issue : result.validationIssues()) {
			categories.add(issue.category());
		}
		return categories;
	}

}
