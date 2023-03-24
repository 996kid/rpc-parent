package com.levi.grpc.etcd.demo;

import lombok.Data;

/**
 * @ClassName: EndPoint
 * @Description: Endpoint
 * @Author: yyh
 * @Date: 2022/12/5 15:52
 */
@Data
public class Endpoint {

    private String addr;

    private int port;

    private String metaData;
}
