package com.cardozojavier.pocmcpserver.mcp.order;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.ai.mcp.annotation.McpTool;

import com.cardozojavier.pocmcpserver.mcp.PaymentGatewayTools;

import static org.assertj.core.api.Assertions.assertThat;

class CreateOrderToolContractTests {

	@Test
	void exposesBuildOrderRequestToolWithNeutralMetadata() throws NoSuchMethodException {
		Method method = PaymentGatewayTools.class.getMethod("buildOrderRequest", Map.class);
		McpTool tool = method.getAnnotation(McpTool.class);

		assertThat(tool).isNotNull();
		assertThat(tool.name()).isEqualTo("build_order_request");
		assertThat(tool.description()).contains("Build and validate an order creation request draft");
		assertThat(tool.description()).contains("without submitting it");
		assertThat(tool.description().toLowerCase()).doesNotContain(("Re" + "vup").toLowerCase());
	}

	@Test
	void buildOrderRequestReturnsContractFields() {
		PaymentGatewayTools tools = new PaymentGatewayTools(new OrderRequestValidator());

		Map<String, Object> result = tools.buildOrderRequest(OrderDraftTestFixtures.validStandardDraft());

		assertThat(result).containsKeys(
				"ready",
				"flowTypes",
				"requestDraft",
				"missingFields",
				"validationIssues",
				"warnings",
				"nextSteps");
		assertThat(result).containsEntry("ready", true);
		assertThat(result).doesNotContainKey("orderId");
	}

	@Test
	void repositoryTextAvoidsVendorSpecificName() throws IOException {
		String vendorName = "Re" + "vup";
		String lowerVendorName = vendorName.toLowerCase();
		Path root = Path.of(System.getProperty("user.dir"));
		List<Path> rootsToScan = List.of(
				root.resolve("src"),
				root.resolve("specs"),
				root.resolve("postman"),
				root.resolve(".specify"),
				root.resolve("AGENTS.md"),
				root.resolve("pom.xml"),
				root.resolve(".gitignore"));

		for (Path scanRoot : rootsToScan) {
			if (!Files.exists(scanRoot)) {
				continue;
			}
			try (Stream<Path> paths = Files.isDirectory(scanRoot) ? Files.walk(scanRoot) : Stream.of(scanRoot)) {
				List<Path> textFiles = paths
					.filter(Files::isRegularFile)
					.filter(path -> !path.toString().contains("target"))
					.toList();
				for (Path textFile : textFiles) {
					String content = Files.readString(textFile);
					assertThat(content)
						.as("vendor-specific text in %s", root.relativize(textFile))
						.doesNotContain(vendorName)
						.doesNotContain(lowerVendorName);
				}
			}
		}
	}

}
