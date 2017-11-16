package com.cokiMing.util;

import com.cokiMing.entity.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by wuyiming on 2017/11/8.
 */
public class Decoder extends ByteToMessageDecoder{

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        //可读字节数小于4，说明读到的消息长度信息不完整
        if (byteBuf.readableBytes() < 8) {
            return;
        }
        byteBuf.markReaderIndex();
        int len = byteBuf.readInt();
        //消息长度信息完整，但是可读字节数小于完整的消息字节长度
        if (byteBuf.readableBytes() < len) {
            byteBuf.resetReaderIndex();
            return;
        }

        int type = byteBuf.readInt();
        byte[] array = new byte[len - 4];
        byteBuf.readBytes(array);
        String content = new String(array);
        Message message = new Message();
        message.setType(type);
        message.setContent(content);
        out.add(message);
    }
}
