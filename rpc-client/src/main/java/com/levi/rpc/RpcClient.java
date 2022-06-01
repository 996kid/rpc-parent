package com.levi.rpc;

import com.levi.rpc.codec.MessageCodecSharable;
import com.levi.rpcmessage.RpcRequestMessage;
import com.levi.rpcmessage.RpcResponseMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * @author 996kid@gmail.com
 * @Description RpcClient
 * @Date 2022/5/31 18:21
 */
@Slf4j
public class RpcClient {

    public static void main(String[] args) {
        try {
            new Bootstrap().group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(// 最大长度，长度偏移，长度占用字节，长度调整，剥离字节数
                                    new LengthFieldBasedFrameDecoder(1024, 8,
                                            4, 0, 0))
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new MessageCodecSharable())
                            .addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    log.debug("sending...");
                                    ByteBuf out = ctx.alloc().buffer();
                                    // 1. 4 字节的魔数
                                    out.writeBytes(new byte[]{1, 2, 3, 4});
                                    // 2. 1 字节的版本,
                                    out.writeByte(1);
                                    // 3. 1 字节的序列化方式 jdk 0 , json 1
                                    out.writeByte(0);
                                    // 无意义，对齐填充 1111 1111 ff
                                    out.writeByte(0xff);
                                    // 填充到8字节
                                    out.writeByte(0xff);

                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                                    oos.writeObject(
                                            RpcRequestMessage.builder()
                                                    .interfaceName("com.levi.api.GreetingService")
                                    .methodName("greeting")
                                                    .variables(new Object[]{"levi"})
                                                    .veriableType(new Class[]{String.class}).build());
                                    byte[] bytes = bos.toByteArray();
                                    // 7. 长度
                                    out.writeInt(bytes.length);
                                    out.writeBytes(bytes);
                                    ctx.writeAndFlush(out);
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    RpcResponseMessage rpcResponseMessage = (RpcResponseMessage) msg;
                                    log.info("result {}", rpcResponseMessage.getResult());
//                                    super.channelRead(ctx, msg);
                                }
                            });
                        }
                    }).connect("127.0.0.1", 8080).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
