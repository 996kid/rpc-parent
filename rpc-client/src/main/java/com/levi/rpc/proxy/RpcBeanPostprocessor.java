package com.levi.rpc.proxy;

import com.levi.rpc.annotation.EnableRpc;
import com.levi.rpc.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * @author 996kid@gmail.com
 * @Description RpcBeanPostprocessor
 * @Date 2022/7/6 21:58
 */
@Component
@Slf4j
public class RpcBeanPostprocessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Annotation[] classAnnotations = bean.getClass().getAnnotations();
        boolean enableRpc = false;
        for (Annotation annotation : classAnnotations) {
            if (annotation.annotationType().equals(EnableRpc.class)) {
                enableRpc = true;
                break;
            }
        }
        if (enableRpc) {
            // 1.查找有RpcService注解的Field
            // 2.生成代理对象
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                // 存在RpcService注解
                if (field.getAnnotationsByType(RpcService.class).length > 0) {
                    field.setAccessible(true);
                    try {
                        field.set(bean, Proxy.newProxyInstance(
                                RpcInvocationHandler.class.getClassLoader(),
                                new Class[]{field.getType()},
                                new RpcInvocationHandler()));
                    } catch (IllegalAccessException e) {
                        log.error("IllegalAccessException: ", e);
                    }
                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }
}
