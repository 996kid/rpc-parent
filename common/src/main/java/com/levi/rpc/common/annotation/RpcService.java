package com.levi.rpc.common.annotation;

import java.lang.annotation.*;

/** 用在类上表名 该类能被rpc调用
 * @author 996kid@gmail.com
 * @Description RpcService
 * @Date 2022/7/8 15:36
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcService {
}
