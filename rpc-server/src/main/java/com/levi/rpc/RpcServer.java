package com.levi.rpc;

import com.levi.rpc.codec.MessageCodecSharable;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 *  rpc框架实现方式： tcp http
 *  1. 公用的api
 *  2. 服务端根据netty接收的数据反射调用api实现类方法，再使用netty返回结果
 *  3. 客户端动态代理api方法， 使用netty client 发送数据
 *  4. 数据序列化和反序列化   jdk json protobuf
 *  5. 数据黏包和半包处理
 *  6. 编码解码器
 * @author 996kid@gmail.com
 * @Description RpcServer
 * @Date 2022/5/30 16:10
 */
@Component
public class RpcServer implements ApplicationRunner {

//    public static void main(String[] args) {
//        NioEventLoopGroup boss = new NioEventLoopGroup(1);
//        NioEventLoopGroup worker = new NioEventLoopGroup();
//        try {
//            ChannelFuture channelFuture = new ServerBootstrap()
//                    .group(boss, worker)
//                    .channel(NioServerSocketChannel.class)
//                    .childHandler(new ChannelInitializer<SocketChannel>() {
//                        protected void initChannel(SocketChannel channel) throws Exception {
//                            // 1. 固定长度 2. 固定分割符 3. data长度 + data
//                            channel.pipeline()
//                                    .addLast(// 最大长度，长度偏移，长度占用字节，长度调整，剥离字节数
//                                            new LengthFieldBasedFrameDecoder(1024, 8,
//                                                    4, 0, 0))
//                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
//                                    .addLast(new MessageCodecSharable())
//                                    .addLast(new RpcHandler());
//                        }
//                    }).bind("127.0.0.1", 8080).sync();
//            channelFuture.channel().closeFuture().sync();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            boss.shutdownGracefully();
//            worker.shutdownGracefully();
//        }
//    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel channel) throws Exception {
                            // 1. 固定长度 2. 固定分割符 3. data长度 + data
                            channel.pipeline()
                                    .addLast(// 最大长度，长度偏移，长度占用字节，长度调整，剥离字节数
                                            new LengthFieldBasedFrameDecoder(1024, 8,
                                                    4, 0, 0))
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new MessageCodecSharable())
                                    .addLast(new RpcHandler());
                        }
                    }).bind("127.0.0.1", 8888).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
