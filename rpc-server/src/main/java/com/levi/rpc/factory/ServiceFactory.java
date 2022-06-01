package com.levi.rpc.factory;

import com.levi.rpc.service.GreetingServiceImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 996kid@gmail.com
 * @Description ServiceFactory
 * @Date 2022/5/31 22:23
 */
public class ServiceFactory {

    private final static Map<String, Object> instanceHolder = new HashMap<>();

    static {
        // 加载接口和其实现类到容器中
        instanceHolder.put("com.levi.api.GreetingService", new GreetingServiceImpl());
    }

    public static Object getInstance(String interfaceName) {
        return instanceHolder.get(interfaceName);
    }

}
