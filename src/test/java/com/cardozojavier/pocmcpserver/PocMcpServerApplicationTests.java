package com.cardozojavier.pocmcpserver;

import com.cardozojavier.pocmcpserver.mcp.PaymentGatewayTools;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PocMcpServerApplicationTests {

	@Autowired
	private PaymentGatewayTools paymentGatewayTools;

	@Test
	void contextLoads() {
		assertThat(paymentGatewayTools).isNotNull();
	}

}
