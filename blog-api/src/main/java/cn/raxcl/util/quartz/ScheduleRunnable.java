package cn.raxcl.util.quartz;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import cn.raxcl.util.common.SpringContextUtils;

import java.lang.reflect.Method;

/**
 * @Description: 执行定时任务
 * @author Raxcl
 * @date 2022-01-07 19:43:01
 */
public class ScheduleRunnable implements Runnable {
	//TODO
	private Object target;
	private Method method;
	private String params;

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
				method.invoke(target);
			}
		} catch (Exception e) {
			throw new RuntimeException("执行定时任务失败", e);
		}
	}
}
