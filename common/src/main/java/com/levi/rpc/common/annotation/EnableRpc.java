package com.levi.rpc.common.annotation;

import java.lang.annotation.*;

/** 用在类上表明该类中的有@RpcInject的属性需要动态代理生成代理对象
 * @author 996kid@gmail.com
 * @Description EnableRpc
 * @Date 2022/7/6 21:57
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableRpc {

}
