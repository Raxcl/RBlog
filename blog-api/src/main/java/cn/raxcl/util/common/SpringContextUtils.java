package cn.raxcl.util.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 *
 * @author Raxcl
 * @date 2022-03-15 19:17:06
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException{
		SpringContextUtils.setContent(applicationContext);
	}

	//todo 看看到底有什么用
	private static void setContent(ApplicationContext applicationContext) {
		SpringContextUtils.applicationContext = applicationContext;
	}

	/**
	 * 通过bean名称获取上下文中的bean
	 * @param name bean名称
	 * @return Object
	 */
	public static Object getBean(String name) {
		return applicationContext.getBean(name);
	}
	public static <T> T getBean(String name, Class<T> requiredType) {
		return applicationContext.getBean(name, requiredType);
	}

	public static boolean containsBean(String name) {
		return applicationContext.containsBean(name);
	}

	public static boolean isSingleton(String name) {
		return applicationContext.isSingleton(name);
	}

	public static Class<? extends Object> getType(String name) {
		return applicationContext.getType(name);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static <T> T getBean(Class<T> requiredType) {
		return applicationContext.getBean(requiredType);
	}
}
