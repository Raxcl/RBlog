package cn.raxcl.aspect;

import cn.raxcl.constant.CodeConstants;
import cn.raxcl.exception.NotFoundException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import cn.raxcl.annotation.OperationLogger;
import cn.raxcl.entity.OperationLog;
import cn.raxcl.service.OperationLogService;
import cn.raxcl.util.AopUtils;
import cn.raxcl.util.IpAddressUtils;
import cn.raxcl.util.JacksonUtils;
import cn.raxcl.util.JwtUtils;
import cn.raxcl.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * AOP记录操作日志
 * @author Raxcl
 * @date 2022-01-04 14:53:25
 */
@Component
@Aspect
public class OperationLogAspect {

	ThreadLocal<Long> currentTime = new ThreadLocal<>();

	private final OperationLogService operationLogService;

	public OperationLogAspect(OperationLogService operationLogService) {
		this.operationLogService = operationLogService;
	}

	/**
	 * 配置切入点
	 */
	@Pointcut("@annotation(operationLogger)")
	public void logPointcut(OperationLogger operationLogger) {
		// 切入点
	}

	/**
	 * 配置环绕通知
	 *
	 * @param joinPoint 切点
	 * @throws Throwable 异常
	 */
	@Around(value = "logPointcut(operationLogger)", argNames = "joinPoint,operationLogger")
	public Object logAround(ProceedingJoinPoint joinPoint, OperationLogger operationLogger) throws Throwable {
		currentTime.set(System.currentTimeMillis());
		Object result = joinPoint.proceed();
		int times = (int) (System.currentTimeMillis() - currentTime.get());
		currentTime.remove();
		OperationLog operationLog = handleLog(joinPoint, operationLogger, times);
		operationLogService.saveOperationLog(operationLog);
		return result;
	}

	/**
	 * 获取HttpServletRequest请求对象，并设置OperationLog对象属性
	 *
	 * @param operationLogger operationLogger
	 * @param times times
	 * @return OperationLog
	 */
	private OperationLog handleLog(ProceedingJoinPoint joinPoint, OperationLogger operationLogger, int times) {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes == null){
			throw new NotFoundException("attributes为空 ---OperationLogAspect.class");
		}
		HttpServletRequest request = attributes.getRequest();
		String username = JwtUtils.getTokenBody(request.getHeader("Authorization"), CodeConstants.SECRET_KEY).getSubject();
		String uri = request.getRequestURI();
		String method = request.getMethod();
		String description = operationLogger.value();
		String ip = IpAddressUtils.getIpAddress(request);
		String userAgent = request.getHeader("User-Agent");
		OperationLog log = new OperationLog(username, uri, method, description, ip, times, userAgent);
		Map<String, Object> requestParams = AopUtils.getRequestParams(joinPoint);
		log.setParam(StringUtils.substring(JacksonUtils.writeValueAsString(requestParams), 0, 2000));
		return log;
	}
}