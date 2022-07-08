package com.levi.rpc.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 996kid@gmail.com
 * @Description ServiceFactory
 * @Date 2022/5/31 22:23
 */
public class RpcServiceFactory {

    private final static Map<String, Object> instanceHolder = new HashMap<>();

    public static void addInstance(String interfaceName, Object instance) {
        instanceHolder.put(interfaceName, instance);
    }

    public static Object getInstance(String interfaceName) {
        return instanceHolder.get(interfaceName);
    }

}
