package com.cokiMing.server;

import com.cokiMing.handler.BlackListHandler;
import com.cokiMing.util.Configuration;
import com.cokiMing.util.Decoder;
import com.cokiMing.util.Encoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * Created by wuyiming on 2017/11/7.
 */
public class BlackListServer {

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,100)
                    .option(ChannelOption.SO_TIMEOUT,10)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new Encoder());
                            socketChannel.pipeline().addLast(new Decoder());
                            socketChannel.pipeline().addLast(new ReadTimeoutHandler(
                                    Integer.parseInt(Configuration.get("cokiMing.megaBlackList.timeout"))));
                            socketChannel.pipeline().addLast(new BlackListHandler());
                        }
                    });
            //绑定端口，等待同步
            ChannelFuture future
                    = bootstrap.bind(Integer.parseInt(Configuration.get("cokiMing.megaBlackList.port"))).sync();
            //等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
