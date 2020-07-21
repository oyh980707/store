package com.loveoyh.store.aop;

import com.loveoyh.store.util.JsonResult;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 访问日志记录
 * @Created by oyh.Jerry to 2020/07/21 16:10
 */
@Aspect
@Component
public class AccessLogAspect {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogAspect.class);

	private final String controllerExecution = "execution(public * com.loveoyh.store.controller..*.*(..))";
	
	private final String serviceExecution = "execution(* com.loveoyh.store.service..*.*(..))";
	
	private final String mapperExecution = "execution(* com.loveoyh.store.mapper.*.*(..))";
	
	/**
	 * controller切点
	 */
	@Pointcut(controllerExecution)
	public void controllerCut() {}
	
	/**
	 * service切点
	 */
	@Pointcut(serviceExecution)
	public void serviceCut() {}
	
	/**
	 * mapper切点
	 */
	@Pointcut(mapperExecution)
	public void mapperCut() {}
	
	//@Before指在切点方法之前执行,也就是在Controller层方法执行之前执行,这里可以通过JoinPoint获取一些有关方法的信息,在这里也可以修改参数的值
	
	/**
	 * 执行controller方法前后
	 * @param joinPoint
	 */
	@Around("controllerCut()")
	public Object doControllerPointCut(ProceedingJoinPoint joinPoint) throws Throwable {
		Long startTime = System.currentTimeMillis();
		
		// 接收到请求，记录请求内容
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		
		// 记录下请求内容
		StringBuffer requestURL = request.getRequestURL();
		String method = request.getMethod();
		String remoteAddr = request.getRemoteAddr();
		String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
		String param = Arrays.toString(joinPoint.getArgs()).replaceAll("\\[|\\]","");
		LOGGER.info("{}({}),URL[{}],HTTP_METHOD[{}],IP[{}]"
				, classMethod, param, requestURL, method, remoteAddr);
		
		//目标业务方法
		Object obj = joinPoint.proceed();
		
		// 处理完请求，返回内容
		LOGGER.info("{} <== RESULT[{}]", classMethod, obj!=null?obj.toString():"");
		LOGGER.info("{} SPEND TIME ==> {} ms", classMethod, System.currentTimeMillis() - startTime);
		
		return obj;
	}
	
	/**
	 * 执行service方法前后
	 * @param joinPoint
	 */
	@Around("serviceCut()")
	public Object doServicePointCut(ProceedingJoinPoint joinPoint) throws Throwable {
		Long startTime = System.currentTimeMillis();
		String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
		String param = Arrays.toString(joinPoint.getArgs()).replaceAll("\\[|\\]","");
		LOGGER.info("{}({})", classMethod, param);
		
		Object obj = joinPoint.proceed();
		
		// 处理完请求，返回内容
		LOGGER.info("{} <== RESULT[{}]", classMethod, obj!=null?obj.toString():"");
		LOGGER.info("{} SPEND TIME ==> {} ms", classMethod, System.currentTimeMillis() - startTime);
		
		return obj;
	}
	
	/**
	 * 执行dao方法前后
	 * @param joinPoint
	 */
	@Around("mapperCut()")
	public Object doMapperPointCut(ProceedingJoinPoint joinPoint) throws Throwable {
		Long startTime = System.currentTimeMillis();
		String classMethod = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
		String param = Arrays.toString(joinPoint.getArgs()).replaceAll("\\[|\\]","");
		LOGGER.info("{}({})", classMethod, param);
		
		Object obj = joinPoint.proceed();
		
		// 处理完请求，返回内容
		LOGGER.info("{} <== RESULT[{}]", classMethod, obj!=null?obj.toString():"");
		LOGGER.info("{} SPEND TIME ==> {} ms", classMethod, System.currentTimeMillis() - startTime);
		
		return obj;
	}
	
}
