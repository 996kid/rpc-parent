package com.levi.rpc.common.rpcmessage;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 996kid@gmail.com
 * @Description RpcResponseMessage
 * @Date 2022/5/30 16:48
 */
@Builder
@Data
public class RpcResponseMessage implements Serializable {

    private String requestId;

    private Object result;

    private Exception exception;

}
