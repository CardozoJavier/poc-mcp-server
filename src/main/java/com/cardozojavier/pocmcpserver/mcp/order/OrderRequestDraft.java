package com.cardozojavier.pocmcpserver.mcp.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record OrderRequestDraft(
		Money money,
		String notificationUrl,
		RedirectUrl redirectUrl,
		UserInformation userInformation,
		String purchaseId,
		String description,
		String concept,
		List<ItemDetail> items,
		String customTag,
		AuthenticationDetails authentication,
		RecurringDetails recurring,
		ProcessorContext processorContext,
		Map<String, Object> rawValues) {

	public record Money(BigDecimal amount, String currency) {
	}

	public record RedirectUrl(String success, String fail, String threeDSChallengeResult) {
	}

	public record UserInformation(
			String ip,
			String email,
			String country,
			String language,
			String customerId,
			BrowserInformation browserInformation,
			String domain) {
	}

	public record BrowserInformation(String userAgentHeader, String acceptHeader, String fingerprint) {
	}

	public record AuthenticationDetails(
			Object threeDSSupport,
			Object cardHolderAccountInformation,
			Object merchantRiskIndicator,
			Object threeDSRequestorAuthenticationInfo,
			Object deviceRenderingOptionsSupported) {
	}

	public record RecurringDetails(
			String type,
			String sequence,
			Integer minPeriod,
			Boolean generateToken,
			String parentTransactionId) {
	}

	public record ItemDetail(String name, String sku, Integer quantity, BigDecimal unitPrice, String url) {
	}

	public record ProcessorContext(String connectionType, Boolean dynamicDescriptorEnabled) {
	}

}
