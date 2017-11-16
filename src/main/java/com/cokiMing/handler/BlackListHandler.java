package com.cokiMing.handler;

import com.cokiMing.entity.Message;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutException;

/**
 * Created by wuyiming on 2017/11/7.
 */
public class BlackListHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到请求，开始处理...");
        Message message = (Message) msg;
        int type = message.getType();
        Message response = new Message();
        BlackListManager manager = BlackListManager.getInstant();
        String[] ipList = message.getContent().split(",");

        switch (type) {
            case Message.DEFRIEND:
                if (manager.defriendIPs(ipList)) {
                    response.setType(Message.SUCCESS);
                    response.setContent("ok");
                }break;
            case Message.CHECK:
                if (manager.checkIPCertification(ipList)) {
                    response.setType(Message.SUCCESS);
                    response.setContent("ok");
                }break;
            case Message.RESUME:
                if (manager.resumeIPs(ipList)) {
                    response.setType(Message.SUCCESS);
                    response.setContent("ok");
                } break;
        }

        if (response.getContent() == null) {
            response.setType(Message.FAIL);
            response.setContent("fail");
        }

        response.fixLen();
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ReadTimeoutException) {
            System.out.println("连接超时,断开...");
//            ((ChannelFuture)ctx).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
