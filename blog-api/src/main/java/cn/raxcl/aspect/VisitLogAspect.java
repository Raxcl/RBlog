package cn.raxcl.aspect;

import cn.raxcl.constant.CodeConstant;
import cn.raxcl.constant.CommonConstant;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import cn.raxcl.annotation.VisitLogger;
import cn.raxcl.constant.RedisKeyConstant;
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
 * @Description: AOP记录访问日志
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
		Object result = joinPoint.proceed();
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
		String identification = request.getHeader(CommonConstant.IDENTIFICATION);
		if (identification == null) {
			//请求头没有uuid，签发uuid并保存到数据库和Redis
			identification = saveUUID(request);
		} else {
			//校验Redis中是否存在uuid
			boolean redisHas = redisService.hasValueInSet(RedisKeyConstant.IDENTIFICATION_SET, identification);
			//Redis中不存在uuid
			if (!redisHas) {
				//校验数据库中是否存在uuid
				boolean mysqlHas = visitorService.hasUuid(identification);
				if (mysqlHas) {
					//数据库存在，保存至Redis
					redisService.saveValueToSet(RedisKeyConstant.IDENTIFICATION_SET, identification);
				} else {
					//数据库不存在，签发新的uuid
					identification = saveUUID(request);
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
	private String saveUUID(HttpServletRequest request) {
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
		String nameUUID = timestamp + ip + userAgent;
		String uuid = UUID.nameUUIDFromBytes(nameUUID.getBytes()).toString();
		//添加访客标识码UUID至响应头
		Objects.requireNonNull(response).addHeader(CommonConstant.IDENTIFICATION, uuid);
		//暴露自定义header供页面资源使用
		response.addHeader("Access-Control-Expose-Headers", CommonConstant.IDENTIFICATION);
		//校验Redis中是否存在uuid
		boolean redisHas = redisService.hasValueInSet(RedisKeyConstant.IDENTIFICATION_SET, uuid);
		if (!redisHas) {
			//保存至Redis
			redisService.saveValueToSet(RedisKeyConstant.IDENTIFICATION_SET, uuid);
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
	private VisitLog handleLog(ProceedingJoinPoint joinPoint, VisitLogger visitLogger, HttpServletRequest request, Object result,
	                           int times, String identification) {
		String uri = request.getRequestURI();
		String method = request.getMethod();
		String behavior = visitLogger.behavior();
		String content = visitLogger.content();
		String ip = IpAddressUtils.getIpAddress(request);
		String userAgent = request.getHeader("User-Agent");
		Map<String, Object> requestParams = AopUtils.getRequestParams(joinPoint);
		Map<String, String> map = judgeBehavior(behavior, content, requestParams, result);
		VisitLog log = new VisitLog(identification, uri, method, behavior, map.get("content"), map.get("remark"), ip, times, userAgent);
		log.setParam(StringUtils.substring(JacksonUtils.writeValueAsString(requestParams), 0, 2000));
		return log;
	}

	/**
	 * 根据访问行为，设置对应的访问内容或备注
	 *
	 * @param behavior behavior
	 * @param content content
	 * @param requestParams requestParams
	 * @param result result
	 * @return Map<String, String>
	 */
	private Map<String, String> judgeBehavior(String behavior, String content, Map<String, Object> requestParams, Object result) {
		Map<String, String> map = new HashMap<>(8);
		String remark = "";
		boolean isViewAndIsFirstPage = CommonConstant.VIEW_PAGE.equals(behavior) && CommonConstant.FIRST_PAGE.equals(content);
		if ( isViewAndIsFirstPage || CommonConstant.DO_NEW.equals(content)) {
			int pageNum = (int) requestParams.get(CommonConstant.PAGE_NUM);
			remark = "第" + pageNum + "页";
		} else if (CommonConstant.VIEW_BLOG.equals(behavior)) {
			Result res = (Result) result;
			if (CodeConstant.SUCCESS.equals(res.getCode())) {
				BlogDetailVO blog = (BlogDetailVO) res.getData();
				String title = blog.getTitle();
				content = title;
				remark = "文章标题：" + title;
			}
		} else if (CommonConstant.SOURCE_BLOG.equals(behavior)) {
			Result res = (Result) result;
			if (CodeConstant.SUCCESS.equals(res.getCode())) {
				String query = (String) requestParams.get("query");
				content = query;
				remark = "搜索内容：" + query;
			}
		} else if (CommonConstant.VIEW_CATEGORY.equals(behavior)) {
			String categoryName = (String) requestParams.get("categoryName");
			int pageNum = (int) requestParams.get(CommonConstant.PAGE_NUM);
			content = categoryName;
			remark = "分类名称：" + categoryName + "，第" + pageNum + "页";
		} else if (CommonConstant.VIEW_TAG.equals(behavior)) {
			String tagName = (String) requestParams.get("tagName");
			int pageNum = (int) requestParams.get(CommonConstant.PAGE_NUM);
			content = tagName;
			remark = "标签名称：" + tagName + "，第" + pageNum + "页";
		} else if (CommonConstant.CLICK_FRIEND.equals(behavior)) {
			String nickname = (String) requestParams.get("nickname");
			content = nickname;
			remark = "友链名称：" + nickname;
		}
		map.put("remark", remark);
		map.put("content", content);
		return map;
	}
}