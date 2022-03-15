package cn.raxcl.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于需要记录访客访问日志的方法
 * @author Raxcl
 * @date 2022-01-07 16:59:57
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VisitLogger {
    /**
     * 访问行为
     */
    String behavior() default "";

    /**
     * 访问内容
     */
    String content() default "";
}
