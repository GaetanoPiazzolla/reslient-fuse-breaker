package gae.piaz.resilience.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FuseBreaker {
    int numberOfAggregatedFailures() default 5;
    String method() default "";  // Method name to be called when breaker is triggered
}