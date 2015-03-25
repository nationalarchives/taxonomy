package uk.gov.nationalarchives.discovery.taxonomy.common.service.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Aspect that handles logging of methods across all layers when they are
 * accordingly annotated
 * 
 * @author jcharlet
 *
 */
@Aspect
@Component
public class MethodLogger {

    /**
     * Log the time spent by a method (for performance analysis)
     * 
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("@annotation(uk.gov.nationalarchives.discovery.taxonomy.common.domain.annotation.Loggable) && anyMethod()")
    public Object logTimeSpentByAMethod(ProceedingJoinPoint point) throws Throwable {
	long start = startTimer();
	Object result = point.proceed();
	Logger logger = LoggerFactory.getLogger(point.getSignature().getDeclaringType());
	long timerDifference = getTimerDifference(start);
	String responseToLog = CollectionUtils.isEmpty(Arrays.asList(point.getArgs())) ? null : point.getArgs()[0]
		.toString();
	if (responseToLog != null && responseToLog.length() > 20) {
	    responseToLog = responseToLog.substring(0, 20);
	}
	if (timerDifference > 1000) {
	    logger.warn("#{}({}): processed in {}ms", MethodSignature.class.cast(point.getSignature()).getMethod()
		    .getName(), responseToLog, timerDifference);
	} else {
	    logger.debug("#{}({}): processed in {}ms", MethodSignature.class.cast(point.getSignature()).getMethod()
		    .getName(), responseToLog, timerDifference);
	}

	return result;
    }

    @Pointcut("execution(private * *(..))")
    public void anyPrivateMethod() {
    }

    @Pointcut("execution(* *(..)) || anyPrivateMethod()")
    public void anyMethod() {
    }

    public static long startTimer() {
	long start_time = System.nanoTime();
	return start_time;
    }

    public static long getTimerDifference(long start_time) {
	long end_time = startTimer();
	return Math.round((end_time - start_time) / 1e6);
    }
}