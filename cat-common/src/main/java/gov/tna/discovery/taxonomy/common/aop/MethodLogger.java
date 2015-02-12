package gov.tna.discovery.taxonomy.common.aop;

import gov.tna.discovery.taxonomy.common.service.tools.TaxonomyHelperTools;

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

@Aspect
@Component
public class MethodLogger {

    @Around("@annotation(gov.tna.discovery.taxonomy.common.aop.annotation.Loggable) && anyMethod()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
	long start = TaxonomyHelperTools.startTimer();
	Object result = point.proceed();
	Logger logger = LoggerFactory.getLogger(point.getSignature().getDeclaringType());
	long timerDifference = TaxonomyHelperTools.getTimerDifference(start);
	String responseToLog = CollectionUtils.isEmpty(Arrays.asList(point.getArgs())) ? null : point.getArgs()[0]
		.toString();
	if (responseToLog.length() > 20) {
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

}