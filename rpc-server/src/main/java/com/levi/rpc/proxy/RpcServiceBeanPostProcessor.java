package com.levi.rpc.proxy;

import com.levi.rpc.RpcServer;
import com.levi.rpc.common.annotation.EnableRpc;
import com.levi.rpc.common.annotation.RpcService;
import com.levi.rpc.factory.RpcServiceFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

/** 处理注解有@RpcService的bean
 * @author 996kid@gmail.com
 * @Description RpcServiceBeanPostProcessor
 * @Date 2022/7/8 15:56
 */
@Component
public class RpcServiceBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Annotation[] classAnnotations = bean.getClass().getAnnotations();
        boolean rpcService = false;
        for (Annotation annotation : classAnnotations) {
            if (annotation.annotationType().equals(RpcService.class)) {
                rpcService = true;
                break;
            }
        }
        if (rpcService) {
            String name = bean.getClass().getInterfaces()[0].getName();
            RpcServiceFactory.addInstance(bean.getClass().getInterfaces()[0].getName(), bean);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }
}
