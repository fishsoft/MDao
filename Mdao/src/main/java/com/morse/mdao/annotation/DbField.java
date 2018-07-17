package com.morse.mdao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段注解
 * Created by morse on 2018/3/26.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbField {
    /**
     * 注解值
     *
     * @return
     */
    String value();

    /**
     * 自增长
     *
     * @return
     */
    boolean autoincrement() default false;
}
