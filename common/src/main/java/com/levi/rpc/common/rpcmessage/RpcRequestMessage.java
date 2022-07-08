package com.levi.rpc.common.rpcmessage;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 996kid@gmail.com
 * @Description RpcRequestMessage
 * @Date 2022/5/30 16:46
 */
@Builder
@Data
public class RpcRequestMessage implements Serializable {

    private String requestId;

    // 接口全限定名
    private String interfaceName;

    private String methodName;

    private Object[] variables;

    private Class[] veriableType;

}
