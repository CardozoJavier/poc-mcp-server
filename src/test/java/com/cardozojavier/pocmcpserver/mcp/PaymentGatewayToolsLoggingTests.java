package com.cardozojavier.pocmcpserver.mcp;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import com.cardozojavier.pocmcpserver.mcp.order.OrderDraftTestFixtures;
import com.cardozojavier.pocmcpserver.mcp.order.OrderRequestValidator;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class PaymentGatewayToolsLoggingTests {

	private final PaymentGatewayTools paymentGatewayTools = new PaymentGatewayTools(new OrderRequestValidator());

	@Test
	void logsSafeNormalizationDetails(CapturedOutput output) {
		paymentGatewayTools.normalizePaymentRequest("usd", new BigDecimal("10.12"));

		assertThat(output).contains("normalize_payment_request");
		assertThat(output).contains("currency=USD");
		assertThat(output).contains("readyForGatewayMapping=true");
	}

	@Test
	void logsSafeOrderDraftValidationDetails(CapturedOutput output) {
		paymentGatewayTools.buildOrderRequest(OrderDraftTestFixtures.validStandardDraft());

		assertThat(output).contains("build_order_request");
		assertThat(output).contains("ready=true");
		assertThat(output).contains("issueCount=0");
		assertThat(output).doesNotContain("buyer@example.com");
		assertThat(output).doesNotContain("93.184.216.34");
		assertThat(output).doesNotContain("browser-fingerprint");
		assertThat(output).doesNotContain("https://merchant.example/success");
	}

}
