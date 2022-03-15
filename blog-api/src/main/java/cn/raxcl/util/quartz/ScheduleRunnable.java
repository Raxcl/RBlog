package cn.raxcl.util.quartz;

import cn.raxcl.exception.NotFoundException;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import cn.raxcl.util.common.SpringContextUtils;

import java.lang.reflect.Method;

/**
 * 执行定时任务
 * @author Raxcl
 * @date 2022-01-07 19:43:01
 */
public class ScheduleRunnable implements Runnable {
	private final Object target;
	private final Method method;
	private final String params;

	public ScheduleRunnable(String beanName, String methodName, String params) throws NoSuchMethodException, SecurityException {
		this.target = SpringContextUtils.getBean(beanName);
		this.params = params;
		if (StringUtils.hasText(params)) {
			this.method = target.getClass().getDeclaredMethod(methodName, String.class);
		} else {
			this.method = target.getClass().getDeclaredMethod(methodName);
		}
	}

	@Override
	public void run() {
		try {
			ReflectionUtils.makeAccessible(method);
			if (StringUtils.hasText(params)) {
				method.invoke(target, params);
			} else {
				//todo 暂时无法解决：少参数提示，暂时无法解决
				method.invoke(target);
			}
		} catch (Exception e) {
			throw new NotFoundException("执行定时任务失败", e);
		}
	}
}
