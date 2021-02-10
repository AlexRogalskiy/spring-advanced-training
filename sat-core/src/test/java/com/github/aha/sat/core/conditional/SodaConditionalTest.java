package com.github.aha.sat.core.conditional;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ConditionalConfig.class)
class SodaConditionalTest extends AbstractConditionalTest {

	@BeforeAll
	static void init() {
		System.setProperty("soda", "aha");
	}
}
