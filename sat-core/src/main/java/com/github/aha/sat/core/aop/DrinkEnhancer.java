package com.github.aha.sat.core.aop;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DrinkEnhancer {

//	@DeclareParents(value = "com.github.aha.sat.core.aop.EnjoyableDrink+", defaultImpl = EnjoyableImpl.class)
	public static Enjoyable enjoyable;

}