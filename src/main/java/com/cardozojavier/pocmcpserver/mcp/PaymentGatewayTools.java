package com.cardozojavier.pocmcpserver.mcp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import com.cardozojavier.pocmcpserver.mcp.order.OrderRequestValidator;
import com.cardozojavier.pocmcpserver.mcp.order.ValidationResult;

@Component
public class PaymentGatewayTools {

	private static final Logger logger = LoggerFactory.getLogger(PaymentGatewayTools.class);

	private final OrderRequestValidator orderRequestValidator;

	public PaymentGatewayTools(OrderRequestValidator orderRequestValidator) {
		this.orderRequestValidator = orderRequestValidator;
	}

	@McpTool(name = "describe_payment_gateway_scope", description = "Describe the current MCP server scope and next implementation milestones.")
	public Map<String, Object> describePaymentGatewayScope() {
		logger.info("tool_invoked name=describe_payment_gateway_scope phase=setup");

		Map<String, Object> response = new LinkedHashMap<>();
		response.put("projectPhase", "setup");
		response.put("transport", "streamable-http-webmvc");
		response.put("status", "The server exposes MCP tools but does not call a payment gateway yet.");
		response.put("nextMilestones", new String[] {
				"choose the target payment gateway API",
				"model authentication and request signing",
				"implement gateway-backed tools with integration tests"
		});
		return response;
	}

	@McpTool(name = "normalize_payment_request", description = "Normalize a payment amount and currency before mapping the request to a gateway-specific API.")
	public Map<String, Object> normalizePaymentRequest(
			@McpToolParam(description = "ISO-4217 currency code", required = true) String currency,
			@McpToolParam(description = "Decimal amount in major currency units", required = true) BigDecimal amount) {

		String normalizedCurrency = currency.trim().toUpperCase(Locale.ROOT);
		BigDecimal normalizedAmount = amount.setScale(2, RoundingMode.HALF_UP);

		Map<String, Object> response = new LinkedHashMap<>();
		response.put("currency", normalizedCurrency);
		response.put("amount", normalizedAmount.toPlainString());
		response.put("minorUnits", normalizedAmount.movePointRight(2).longValueExact());
		response.put("readyForGatewayMapping", true);

		logger.info(
			"tool_invoked name=normalize_payment_request currency={} readyForGatewayMapping={}",
			normalizedCurrency,
			response.get("readyForGatewayMapping"));
		return response;
	}

	@McpTool(name = "build_order_request", description = "Build and validate an order creation request draft for standard, authentication-enhanced, and recurring payment flows without submitting it.")
	public Map<String, Object> buildOrderRequest(
			@McpToolParam(description = "Order creation request draft", required = true) Map<String, Object> requestDraft) {

		ValidationResult result = orderRequestValidator.validate(requestDraft);

		logger.info(
			"tool_invoked name=build_order_request ready={} flowTypes={} missingFieldCount={} issueCount={}",
			result.ready(),
			result.flowTypes(),
			result.missingFields().size(),
			result.validationIssues().size());

		return result.toMap();
	}

}
