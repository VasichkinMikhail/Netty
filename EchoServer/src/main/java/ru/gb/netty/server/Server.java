package ru.gb.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import ru.gb.netty.handler.JsonDecoder;
import ru.gb.netty.handler.JsonEncoder;


public class Server {
    public static void main(String[] args) throws InterruptedException {

        new Server().run();
    }
    public void run() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workersGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(bossGroup, workersGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {

                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(512, 0, 2, 0, 2),
                                    new LengthFieldPrepender(2),
                                    new JsonEncoder(),
                                    new JsonDecoder(),
//                                    new ByteArrayDecoder(),
//                                    new ByteArrayEncoder(),
//                                    new ServerStringDecoder(),
//                                    new ServerStringEncoder(),
                                    new ServerChannelInboundHandlerAdapter()
                            );
                        }

                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            Channel channel = serverBootstrap.bind(9020).sync().channel();
            System.out.println("Server started");
            channel.closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workersGroup.shutdownGracefully();
        }


    }
}
