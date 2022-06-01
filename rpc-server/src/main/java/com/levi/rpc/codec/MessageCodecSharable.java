package com.levi.rpc.codec;

import com.levi.rpcmessage.RpcRequestMessage;
import com.levi.rpcmessage.RpcResponseMessage;
import com.levi.serializer.SerializerAlgorithm;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @author 996kid@gmail.com
 * @Description MessageCodecSharable
 * @Date 2022/5/31 16:35
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, RpcResponseMessage> {

    protected void encode(ChannelHandlerContext ctx, RpcResponseMessage in, List<Object> outList) throws Exception {
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
        // 4. 获取内容的字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(in);
        byte[] bytes = bos.toByteArray();
        // 7. 长度
        out.writeInt(bytes.length);
        // 8. 写入内容
        out.writeBytes(bytes);
        outList.add(out);
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        in.readByte();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        SerializerAlgorithm serializerAlgorithm = SerializerAlgorithm.getByInt(serializerType);
        RpcRequestMessage rpcRequestMessage = serializerAlgorithm.deserialize(RpcRequestMessage.class, bytes);
//        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
//        Object message = ois.readObject();
        out.add(rpcRequestMessage);
    }
}
