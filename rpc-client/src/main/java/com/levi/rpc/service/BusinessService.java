package com.levi.rpc.service;

import com.levi.api.GreetingService;
import com.levi.rpc.annotation.EnableRpc;
import com.levi.rpc.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * @author 996kid@gmail.com
 * @Description BusinessService
 * @Date 2022/7/6 17:58
 */
@Service
@EnableRpc
public class BusinessService {

    @RpcService
    private GreetingService greetingService;

    public void processBusiness() {
        // RPC 调用
        greetingService.greeting("l3vi");
    }
}
