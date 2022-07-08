package com.levi.rpc.common.exception;

/**
 * @author 996kid@gmail.com
 * @Description RpcTimeoutException
 * @Date 2022/7/8 14:59
 */
public class RpcTimeoutException extends Exception {

    public RpcTimeoutException(String message) {
        super(message);
    }
}
