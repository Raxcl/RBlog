package cn.raxcl.aspect;

import cn.raxcl.constant.CommonConstants;
import cn.raxcl.constant.JwtConstants;
import cn.raxcl.enums.VisitBehavior;
import cn.raxcl.model.dto.VisitLogRemark;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import cn.raxcl.annotation.VisitLogger;
import cn.raxcl.constant.RedisKeyConstants;
import cn.raxcl.entity.VisitLog;
import cn.raxcl.entity.Visitor;
import cn.raxcl.model.vo.BlogDetailVO;
import cn.raxcl.util.common.Result;
import cn.raxcl.service.RedisService;
import cn.raxcl.service.VisitLogService;
import cn.raxcl.service.VisitorService;
import cn.raxcl.util.AopUtils;
import cn.raxcl.util.IpAddressUtils;
import cn.raxcl.util.JacksonUtils;
import cn.raxcl.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * AOP记录访问日志
 * @author Raxcl
 * @date 2022-01-07 17:08:31
 */
@Component
@Aspect
public class VisitLogAspect {
	private final VisitLogService visitLogService;
	private final VisitorService visitorService;
	private final RedisService redisService;

	ThreadLocal<Long> currentTime = new ThreadLocal<>();

	public VisitLogAspect(VisitLogService visitLogService, VisitorService visitorService, RedisService redisService) {
		this.visitLogService = visitLogService;
		this.visitorService = visitorService;
		this.redisService = redisService;
	}

	/**
	 * 配置切入点
	 */
	@Pointcut("@annotation(visitLogger)")
	public void logPointcut(VisitLogger visitLogger) {
		// 切入点
	}

	/**
	 * 配置环绕通知
	 *
	 * @param joinPoint joinPoint
	 * @return Object
	 * @throws Throwable 异常
	 */
	@Around(value = "logPointcut(visitLogger)", argNames = "joinPoint,visitLogger")
	public Object logAround(ProceedingJoinPoint joinPoint, VisitLogger visitLogger) throws Throwable {
		currentTime.set(System.currentTimeMillis());
		Result result = (Result) joinPoint.proceed();
		int times = (int) (System.currentTimeMillis() - currentTime.get());
		currentTime.remove();
		//获取请求对象
		HttpServletRequest request = Objects.requireNonNull(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())).getRequest();
		//校验访客标识码
		String identification = checkIdentification(request);
		//记录访问日志
		VisitLog visitLog = handleLog(joinPoint, visitLogger, request, result, times, identification);
		visitLogService.saveVisitLog(visitLog);
		return result;
	}

	/**
	 * 校验访客标识码
	 *
	 * @param request request
	 * @return String
	 */
	private String checkIdentification(HttpServletRequest request) {
		String identification = request.getHeader(CommonConstants.IDENTIFICATION);
		if (identification == null) {
			//请求头没有uuid，签发uuid并保存到数据库和Redis
			identification = saveUuid(request);
		} else {
			//校验Redis中是否存在uuid
			boolean redisHas = redisService.hasValueInSet(RedisKeyConstants.IDENTIFICATION_SET, identification);
			//Redis中不存在uuid
			if (!redisHas) {
				//校验数据库中是否存在uuid
				boolean mysqlHas = visitorService.hasUuid(identification);
				if (mysqlHas) {
					//数据库存在，保存至Redis
					redisService.saveValueToSet(RedisKeyConstants.IDENTIFICATION_SET, identification);
				} else {
					//数据库不存在，签发新的uuid
					identification = saveUuid(request);
				}
			}
		}
		return identification;
	}

	/**
	 * 签发UUID，并保存至数据库和Redis
	 *
	 * @param request request
	 * @return String
	 */
	private String saveUuid(HttpServletRequest request) {
		//获取响应对象
		HttpServletResponse response = Objects.requireNonNull(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())).getResponse();
		//获取当前时间戳，精确到小时，防刷访客数据
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		String timestamp = Long.toString(calendar.getTimeInMillis() / 1000);
		//获取访问者基本信息
		String ip = IpAddressUtils.getIpAddress(request);
		String userAgent = request.getHeader("User-Agent");
		//根据时间戳、ip、userAgent生成UUID
		String nameUuid = timestamp + ip + userAgent;
		String uuid = UUID.nameUUIDFromBytes(nameUuid.getBytes()).toString();
		//添加访客标识码UUID至响应头
		Objects.requireNonNull(response).addHeader(CommonConstants.IDENTIFICATION, uuid);
		//暴露自定义header供页面资源使用
		response.addHeader(CommonConstants.ACCESS_CONTROL_EXPOSE_HEADERS, CommonConstants.IDENTIFICATION);
		//校验Redis中是否存在uuid
		boolean redisHas = redisService.hasValueInSet(RedisKeyConstants.IDENTIFICATION_SET, uuid);
		if (!redisHas) {
			//保存至Redis
			redisService.saveValueToSet(RedisKeyConstants.IDENTIFICATION_SET, uuid);
			//保存至数据库
			Visitor visitor = new Visitor(uuid, ip, userAgent);
			visitorService.saveVisitor(visitor);
		}
		return uuid;
	}

	/**
	 * 设置VisitLogger对象属性
	 *
	 * @param joinPoint joinPoint
	 * @param visitLogger visitLogger
	 * @param result result
	 * @param times times
	 * @return VisitLog
	 */
	private VisitLog handleLog(ProceedingJoinPoint joinPoint, VisitLogger visitLogger, HttpServletRequest request, Result result,
							   int times, String identification) {
		String uri = request.getRequestURI();
		String method = request.getMethod();
		String ip = IpAddressUtils.getIpAddress(request);
		String userAgent = request.getHeader("User-Agent");
		Map<String, Object> requestParams = AopUtils.getRequestParams(joinPoint);
		VisitLogRemark visitLogRemark = judgeBehavior(visitLogger.value(), requestParams, result);
		VisitLog log = new VisitLog(identification, uri, method, visitLogger.value().getBehavior(),
				visitLogRemark.getContent(), visitLogRemark.getRemark(), ip, times, userAgent);
		log.setParam(StringUtils.substring(JacksonUtils.writeValueAsString(requestParams), 0, 2000));
		return log;
	}

	/**
	 * 根据访问行为，设置对应的访问内容或备注
	 *
	 * @param behavior behavior
	 * @param requestParams requestParams
	 * @param result result
	 * @return VisitLogRemark
	 */
	private VisitLogRemark judgeBehavior(VisitBehavior behavior, Map<String, Object> requestParams, Result result) {
		String remark = "";
		String content = behavior.getContent();
		switch (behavior) {
			case INDEX:
			case MOMENT:
				remark = "第" + requestParams.get(CommonConstants.PAGE_NUM) + "页";
				break;
			case BLOG:
				if (JwtConstants.SUCCESS.equals(result.getCode())) {
					BlogDetailVO blog = (BlogDetailVO) result.getData();
					String title = blog.getTitle();
					content = title;
					remark = "文章标题：" + title;
				}
				break;
			case SEARCH:
				if (JwtConstants.SUCCESS.equals(result.getCode())) {
					String query = (String) requestParams.get("query");
					content = query;
					remark = "搜索内容：" + query;
				}
				break;
			case CATEGORY:
				String categoryName = (String) requestParams.get("categoryName");
				content = categoryName;
				remark = "分类名称：" + categoryName + "，第" + requestParams.get("pageNum") + "页";
				break;
			case TAG:
				String tagName = (String) requestParams.get("tagName");
				content = tagName;
				remark = "标签名称：" + tagName + "，第" + requestParams.get("pageNum") + "页";
				break;
			case CLICK_FRIEND:
				String nickname = (String) requestParams.get("nickname");
				content = nickname;
				remark = "友链名称：" + nickname;
				break;
			default:
				break;
		}
		return new VisitLogRemark(content, remark);
	}
}