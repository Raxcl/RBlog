package cn.raxcl.util.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 *
 * @author Raxcl
 * @date 2022-01-07 18:58:02
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException{
		SpringContextUtils.setContent(applicationContext);
	}

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
}
