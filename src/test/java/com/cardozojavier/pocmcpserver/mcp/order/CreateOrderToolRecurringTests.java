package com.cardozojavier.pocmcpserver.mcp.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateOrderToolRecurringTests {

	private final OrderRequestValidator validator = new OrderRequestValidator();

	@Test
	void acceptsInitialRecurringSetupWithTokenGeneration() {
		ValidationResult result = validator.validate(OrderDraftTestFixtures.validInitialRecurringDraft());

		assertThat(result.ready()).isTrue();
		assertThat(result.flowTypes()).containsExactly("standard", "recurring");
		assertThat(result.requestDraft()).containsKey("recurring");
		assertThat(result.validationIssues()).isEmpty();
	}

	@Test
	void acceptsRecurringAndFinalSequencesWithParentTransactionAndNoTokenGeneration() {
		ValidationResult recurring = validator.validate(OrderDraftTestFixtures.validRecurringDraft());
		ValidationResult finalSequence = validator.validate(OrderDraftTestFixtures.validFinalRecurringDraft());

		assertThat(recurring.ready()).isTrue();
		assertThat(finalSequence.ready()).isTrue();
		assertThat(recurring.flowTypes()).containsExactly("standard", "recurring");
		assertThat(finalSequence.flowTypes()).containsExactly("standard", "recurring");
	}

	@Test
	void requiresParentTransactionForRecurringAndFinalSequences() {
		Map<String, Object> draft = OrderDraftTestFixtures.validRecurringDraft();
		@SuppressWarnings("unchecked")
		Map<String, Object> recurring = (Map<String, Object>) draft.get("recurring");
		recurring.remove("parentTransactionId");

		ValidationResult result = validator.validate(draft);

		assertThat(result.ready()).isFalse();
		assertThat(result.missingFields()).contains("recurring.parentTransactionId");
		assertThat(issueCategories(result)).contains("invalid_recurring_reference");
	}

	@Test
	void rejectsNewTokenGenerationForRecurringAndFinalSequences() {
		Map<String, Object> draft = OrderDraftTestFixtures.validFinalRecurringDraft();
		@SuppressWarnings("unchecked")
		Map<String, Object> recurring = (Map<String, Object>) draft.get("recurring");
		recurring.put("generateToken", true);

		ValidationResult result = validator.validate(draft);

		assertThat(result.ready()).isFalse();
		assertThat(issueCategories(result)).contains("invalid_recurring_sequence");
	}

	@Test
	void rejectsInvalidMinimumPeriodAndSequence() {
		Map<String, Object> draft = OrderDraftTestFixtures.validInitialRecurringDraft();
		@SuppressWarnings("unchecked")
		Map<String, Object> recurring = (Map<String, Object>) draft.get("recurring");
		recurring.put("minPeriod", 0);
		recurring.put("sequence", "paused");

		ValidationResult result = validator.validate(draft);

		assertThat(result.ready()).isFalse();
		assertThat(issueCategories(result)).contains("invalid_recurring_sequence");
	}

	private static List<String> issueCategories(ValidationResult result) {
		List<String> categories = new ArrayList<>();
		for (ValidationIssue issue : result.validationIssues()) {
			categories.add(issue.category());
		}
		return categories;
	}

}
