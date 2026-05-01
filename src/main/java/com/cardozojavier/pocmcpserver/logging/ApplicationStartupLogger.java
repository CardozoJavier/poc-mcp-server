package com.cardozojavier.pocmcpserver.logging;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupLogger {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupLogger.class);

	private final Environment environment;
	private final String applicationName;
	private final String transport;

	public ApplicationStartupLogger(
			Environment environment,
			@Value("${spring.application.name}") String applicationName,
			@Value("${spring.ai.mcp.server.protocol}") String transport) {
		this.environment = environment;
		this.applicationName = applicationName;
		this.transport = transport;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void logApplicationStarted() {
		String profiles = Arrays.stream(environment.getActiveProfiles())
			.sorted()
			.collect(Collectors.joining(","));

		if (profiles.isBlank()) {
			profiles = "default";
		}

		logger.info(
			"application_started name={} profiles={} transport={}",
			applicationName,
			profiles,
			transport);
	}

}
