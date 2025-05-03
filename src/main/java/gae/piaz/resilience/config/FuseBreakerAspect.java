package gae.piaz.resilience.config;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class FuseBreakerAspect {

    private static final Map<String, AtomicInteger> failureCounters = new ConcurrentHashMap<>();
    private static final Map<String, Boolean> permanentlyBroken = new ConcurrentHashMap<>();

    @Around("@annotation(fuseBreaker)")
    public Object handlePermanentBreaker(ProceedingJoinPoint joinPoint, FuseBreaker fuseBreaker) throws Throwable {
        String methodKey = joinPoint.getSignature().toLongString();

        // Check if already broken
        if (Boolean.TRUE.equals(permanentlyBroken.get(methodKey))) {
            throw new FuseBreakerException("Method permanently disabled after multiple failures");
        }

        try {
            Object returned = joinPoint.proceed();
            failureCounters.remove(methodKey);
            return returned;
        } catch (Exception e) {

            // Count the failure
            int failures = failureCounters.computeIfAbsent(methodKey, k -> new AtomicInteger(0))
                .incrementAndGet();

            // Check threshold
            if (failures >= fuseBreaker.numberOfAggregatedFailures()) {
                permanentlyBroken.put(methodKey, true);
                // Execute callback method if specified
                String callbackMethodName = fuseBreaker.method();
                if (!callbackMethodName.isEmpty()) {
                    try {
                        Object target = joinPoint.getTarget();
                        Method callback = target.getClass().getDeclaredMethod(callbackMethodName);
                        callback.setAccessible(true);
                        callback.invoke(target);
                    } catch (Exception ex) {
                        log.error("Failed to execute fuse-breaker method", ex);
                        permanentlyBroken.put(methodKey, false);
                    }
                }
            }

            throw e;
        }
    }
}