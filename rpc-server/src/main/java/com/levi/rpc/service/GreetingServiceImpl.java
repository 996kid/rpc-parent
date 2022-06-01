package com.levi.rpc.service;

import com.levi.api.GreetingService;

/**
 * @author 996kid@gmail.com
 * @Description GreetingServiceImpl
 * @Date 2022/5/30 16:33
 */
public class GreetingServiceImpl implements GreetingService {

    public String greeting(String name) {
        return "hello, " + name;
    }
}
