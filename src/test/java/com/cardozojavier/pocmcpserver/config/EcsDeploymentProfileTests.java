package com.cardozojavier.pocmcpserver.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.profiles.active=ecs")
@AutoConfigureMockMvc
class EcsDeploymentProfileTests {

	@Autowired
	private Environment environment;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void ecsProfileUsesContainerFriendlyServerSettings() {
		assertThat(environment.getProperty("server.address")).isEqualTo("0.0.0.0");
		assertThat(environment.getProperty("server.port")).isEqualTo("8080");
		assertThat(environment.getProperty("server.shutdown")).isEqualTo("graceful");
		assertThat(environment.getProperty("spring.lifecycle.timeout-per-shutdown-phase")).isEqualTo("20s");
	}

	@Test
	void readinessEndpointIsPublicForEcsHealthChecks() throws Exception {
		mockMvc.perform(get("/actuator/health/readiness"))
			.andExpect(status().isOk());
	}

}
