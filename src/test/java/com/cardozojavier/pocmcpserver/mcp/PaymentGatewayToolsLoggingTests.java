package com.cardozojavier.pocmcpserver.mcp;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class PaymentGatewayToolsLoggingTests {

	private final PaymentGatewayTools paymentGatewayTools = new PaymentGatewayTools();

	@Test
	void logsSafeNormalizationDetails(CapturedOutput output) {
		paymentGatewayTools.normalizePaymentRequest("usd", new BigDecimal("10.12"));

		assertThat(output).contains("normalize_payment_request");
		assertThat(output).contains("currency=USD");
		assertThat(output).contains("readyForGatewayMapping=true");
	}

}
