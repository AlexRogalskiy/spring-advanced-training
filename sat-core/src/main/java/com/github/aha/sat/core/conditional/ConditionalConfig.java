package com.github.aha.sat.core.conditional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.github.aha.sat.core.conditional.condition.AlcoholCondition;
import com.github.aha.sat.core.conditional.condition.SodaCondition;
import com.github.aha.sat.core.wiring.beverage.Beverage;

@Configuration
@ComponentScan
public class ConditionalConfig {

	@Bean
	@Conditional(AlcoholCondition.class)
	public Beverage beer()	{
		return () -> "Beer";
	}

	@Bean
	@Conditional(AlcoholCondition.class)
	public Beverage wine() {
		return () -> "Wine";
	}

	@Bean
	@Conditional(SodaCondition.class)
	public Beverage cola() {
		return () -> "cola";
	}

	@Bean
	@Conditional(SodaCondition.class)
	public Beverage fanta() {
		return () -> "fanta";
	}

}
