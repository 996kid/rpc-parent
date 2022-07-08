package com.levi.rpc;

import com.levi.rpc.common.rpcmessage.RpcRequestMessage;
import com.levi.rpc.common.rpcmessage.RpcResponseMessage;
import com.levi.rpc.factory.RpcServiceFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author 996kid@gmail.com
 * @Description RpcHandler
 * @Date 2022/5/30 16:54
 */
@Slf4j
public class RpcHandler extends ChannelInboundHandlerAdapter {



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("msg {}", msg);
        RpcRequestMessage rpcRequestMessage = (RpcRequestMessage) msg;
        log.info("invoke {}.{}, variables: {}", rpcRequestMessage.getInterfaceName(), rpcRequestMessage.getMethodName(), rpcRequestMessage.getVariables());
        // todo 根据接口名称 方法名称 参数列表利用反射调用实现类的方法
        Object obj = RpcServiceFactory
                .getInstance(rpcRequestMessage.getInterfaceName());
        Method method = obj
                .getClass()
                .getMethod(rpcRequestMessage.getMethodName(), rpcRequestMessage.getVeriableType());
        Object result = method.invoke(obj, rpcRequestMessage.getVariables());
        ctx.writeAndFlush(RpcResponseMessage.builder()
                .result(result)
                .requestId(rpcRequestMessage.getRequestId())
                .build());
//        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
