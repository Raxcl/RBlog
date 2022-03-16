package cn.raxcl.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import cn.raxcl.annotation.OperationLogger;
import cn.raxcl.annotation.VisitLogger;
import cn.raxcl.entity.ExceptionLog;
import cn.raxcl.service.ExceptionLogService;
import cn.raxcl.util.AopUtils;
import cn.raxcl.util.IpAddressUtils;
import cn.raxcl.util.JacksonUtils;
import cn.raxcl.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * AOP记录异常日志
 * @author Raxcl
 * @date 2022-01-07 17:00:37
 */
@Component
@Aspect
public class ExceptionLogAspect {
	private final ExceptionLogService exceptionLogService;

	public ExceptionLogAspect(ExceptionLogService exceptionLogService) {
		this.exceptionLogService = exceptionLogService;
	}

	/**
	 * 配置切入点
	 */
	@Pointcut("execution(* cn.raxcl.controller..*.*(..))")
	public void logPointcut() {
		// 切入点
	}

	@AfterThrowing(value = "logPointcut()", throwing = "e")
	public void logAfterThrowing(JoinPoint joinPoint, Exception e) {
		ExceptionLog log = handleLog(joinPoint, e);
		exceptionLogService.saveExceptionLog(log);
	}

	/**
	 * 设置ExceptionLog对象属性
	 *
	 * @return ExceptionLog
	 */
	private ExceptionLog handleLog(JoinPoint joinPoint, Exception e) {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
		String uri = request.getRequestURI();
		String method = request.getMethod();
		String ip = IpAddressUtils.getIpAddress(request);
		String userAgent = request.getHeader("User-Agent");
		//todo 使用swagger后，可以直接使用注解上的内容作为 ExceptionLog 的 description
		String description = getDescriptionFromAnnotations(joinPoint);
		String error = StringUtils.getStackTrace(e);
		ExceptionLog log = new ExceptionLog(uri, method, description, error, ip, userAgent);
		Map<String, Object> requestParams = AopUtils.getRequestParams(joinPoint);
		log.setParam(StringUtils.substring(JacksonUtils.writeValueAsString(requestParams), 0, 2000));
		return log;
	}

	private String getDescriptionFromAnnotations(JoinPoint joinPoint) {
		Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
		OperationLogger operationLogger = method.getAnnotation(OperationLogger.class);
		if (operationLogger != null) {
			return operationLogger.value();
		}
		VisitLogger visitLogger = method.getAnnotation(VisitLogger.class);
		if (visitLogger != null) {
			return visitLogger.value().getBehavior();
		}
		return "";
	}
}