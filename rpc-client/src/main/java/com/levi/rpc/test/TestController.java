package com.levi.rpc.test;

import com.levi.rpc.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 996kid@gmail.com
 * @Description TestController
 * @Date 2022/7/8 14:32
 */
@RestController
public class TestController {

    @Autowired
    private BusinessService businessService;

    @GetMapping("test")
    public String test() {
        return businessService.processBusiness();
    }
}
