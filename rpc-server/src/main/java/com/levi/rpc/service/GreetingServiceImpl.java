package com.levi.rpc.service;

import com.levi.api.GreetingService;
import com.levi.rpc.common.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * @author 996kid@gmail.com
 * @Description GreetingServiceImpl
 * @Date 2022/5/30 16:33
 */
@RpcService
@Service
public class GreetingServiceImpl implements GreetingService {

    public String greeting(String name) {
        return "hello, " + name;
    }
}
