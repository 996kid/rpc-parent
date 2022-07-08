package com.levi.rpc.service;

import com.levi.api.GreetingService;
import com.levi.rpc.common.annotation.EnableRpc;
import com.levi.rpc.common.annotation.RpcInject;
import org.springframework.stereotype.Service;

/**
 * @author 996kid@gmail.com
 * @Description BusinessService
 * @Date 2022/7/6 17:58
 */
@Service
@EnableRpc
public class BusinessService {

    @RpcInject
    private GreetingService greetingService;

    /**
     * RPC调用完成业务
     */
    public String processBusiness() {
        // RPC 调用
        return greetingService.greeting("l3vi");
    }
}
