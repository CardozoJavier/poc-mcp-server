package com.cardozojavier.pocmcpserver.mcp.order;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record ValidationResult(
		boolean ready,
		List<String> flowTypes,
		Map<String, Object> requestDraft,
		List<String> missingFields,
		List<ValidationIssue> validationIssues,
		List<String> warnings,
		List<String> nextSteps) {

	public Map<String, Object> toMap() {
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("ready", ready);
		response.put("flowTypes", flowTypes);
		response.put("requestDraft", requestDraft);
		response.put("missingFields", missingFields);
		response.put("validationIssues", validationIssues.stream().map(ValidationIssue::toMap).toList());
		response.put("warnings", warnings);
		response.put("nextSteps", nextSteps);
		return response;
	}

}
