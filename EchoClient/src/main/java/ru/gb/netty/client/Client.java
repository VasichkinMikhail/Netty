package ru.gb.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.util.Date;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        new Client().run();

    }

    public void run() throws InterruptedException{
        NioEventLoopGroup worker = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(512, 0, 2, 0, 2),
                                    new LengthFieldPrepender(2),
                                    new StringEncoder(),
                                    new StringDecoder(),
                                    new SimpleChannelInboundHandler<String>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                            System.out.println("Incoming message server: " + msg);
                                        }
                                    }

                            );
                        }
                    })

                    .option(ChannelOption.SO_KEEPALIVE, true);

            Channel channel = bootstrap.connect("localhost", 9020).sync().channel();
            while (true) {
                channel.writeAndFlush("Hello world!!! ");
                Thread.sleep(5000);

                }
        } finally{
            worker.shutdownGracefully();
        }
    }
}
