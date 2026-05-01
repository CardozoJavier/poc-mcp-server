package com.cardozojavier.pocmcpserver.logging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "test.startup-logger=true")
@ExtendWith(OutputCaptureExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ApplicationStartupLoggerTests {

	@Test
	void logsApplicationStartupDetails(CapturedOutput output) {
		assertThat(output).contains("application_started");
		assertThat(output).contains("name=poc-mcp-server");
		assertThat(output).contains("transport=STREAMABLE");
	}

}
