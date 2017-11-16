package com.cokiMing.util;

import com.cokiMing.entity.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by wuyiming on 2017/11/8.
 */
public class Encoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        byte[] bytes = message.getContent().getBytes();
        int type = message.getType();
        byteBuf.writeInt(message.getLen());
        byteBuf.writeInt(type);
        byteBuf.writeBytes(bytes);
    }
}
