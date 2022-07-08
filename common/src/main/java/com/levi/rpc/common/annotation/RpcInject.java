package com.levi.rpc.common.annotation;

import java.lang.annotation.*;

/**
 * 用在属性上表明该属性会生成代理对象
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcInject {

    String value() default "";
}
