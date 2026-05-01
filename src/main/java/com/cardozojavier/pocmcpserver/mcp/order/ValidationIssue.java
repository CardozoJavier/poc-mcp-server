package com.cardozojavier.pocmcpserver.mcp.order;

import java.util.LinkedHashMap;
import java.util.Map;

public record ValidationIssue(String field, String category, String message) {

	public Map<String, Object> toMap() {
		Map<String, Object> issue = new LinkedHashMap<>();
		issue.put("field", field);
		issue.put("category", category);
		issue.put("message", message);
		return issue;
	}

}
