package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *  自动填充注解
 *  用于标记需要自动填充创建人、创建时间、修改人、修改时间等字段的方法
 */
@Target(ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //数据库操作类型 update或insert
    OperationType value();
}
