package com.levi.rpc.proxy;

import com.levi.api.GreetingService;
import com.levi.rpc.codec.MessageCodecSharable;
import com.levi.rpc.common.exception.RpcTimeoutException;
import com.levi.rpc.common.rpcmessage.RpcRequestMessage;
import com.levi.rpc.common.rpcmessage.RpcResponseMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author 996kid@gmail.com
 * @Description RpcInvocationHandler
 * @Date 2022/6/1 10:04
 */
@Slf4j
public class RpcInvocationHandler implements InvocationHandler {

    public static Map<String, Promise<RpcResponseMessage>> requestPromise = new HashMap<>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        System.out.println("hello, " + args[0]);
//        System.out.println(method.getDeclaringClass().getName());
//        System.out.println(method.getName());
//        System.out.println(method.getParameterTypes());
//         动态代理api接口调用 -> 异步tcp转同步等待结果返回
        ChannelFuture channelFuture = new Bootstrap().group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        log.info("connected...");
                        ch.pipeline().addLast(// 最大长度，长度偏移，长度占用字节，长度调整，剥离字节数
                                new LengthFieldBasedFrameDecoder(1024, 8,
                                        4, 0, 0))
                                .addLast(new LoggingHandler(LogLevel.DEBUG))
                                .addLast(new MessageCodecSharable())
                                .addLast(new SimpleChannelInboundHandler<RpcResponseMessage>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
                                        // 读取返回结果
                                        log.info("channelRead0: {}", msg);
                                        Promise result = requestPromise.remove(msg.getRequestId());
                                        if (msg.getException() != null) {
                                            result.setFailure(msg.getException());
                                        } else {
                                            result.setSuccess(msg);
                                        }
                                    }
                                });
                    }
                }).connect("127.0.0.1", 8888).sync();
        Channel channel = channelFuture.channel();
        Promise<RpcResponseMessage> promise = new DefaultPromise(channel.eventLoop());
        String requestId = UUID.randomUUID().toString();
        requestPromise.put(requestId, promise);
        channel.writeAndFlush(RpcRequestMessage.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .veriableType(method.getParameterTypes())
                .variables(args)
                .requestId(requestId)
                .build()
        );
        long before = System.currentTimeMillis();
        // promise: 代表一个异步任务
        promise.await(5000);
        long after = System.currentTimeMillis();
        if (after - before > 5000) {
            log.error("response timeout");
            throw new RpcTimeoutException("response timeout");
        }
        if (promise.isSuccess()) {
            RpcResponseMessage responseMessage = promise.getNow();
            log.info("result: {}", responseMessage.getResult());
            return responseMessage.getResult();
        } else {
            log.error("invoke failed: {}", promise.cause());
        }
//        method.invoke(proxy, args);
        return null;
    }

    public static void main(String[] args) {
        GreetingService greetingService = (GreetingService) Proxy.newProxyInstance(
                RpcInvocationHandler.class.getClassLoader(),
                new Class[]{GreetingService.class},
                new RpcInvocationHandler());
        greetingService.greeting("levi");
    }
}
