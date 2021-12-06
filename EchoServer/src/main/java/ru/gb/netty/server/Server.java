package ru.gb.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.ssl.ClientAuth;
import ru.gb.netty.database.AuthClient;
import ru.gb.netty.database.AuthLoginPassword;
import ru.gb.netty.database.AuthService;
import ru.gb.netty.database.DataBase;
import ru.gb.netty.handler.JsonDecoder;
import ru.gb.netty.handler.JsonEncoder;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;


public class Server {

    public static void main(String[] args) throws SQLException, InterruptedException {
        new Server().run();
    }

    public void run() throws InterruptedException, SQLException {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workersGroup = new NioEventLoopGroup();
        ExecutorService threadPool = Executors.newCachedThreadPool();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(bossGroup, workersGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {

                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {


                            ch.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024*1024, 0, 3, 0, 3),//погуглим
                                    new LengthFieldPrepender(3),
                                    new JsonEncoder(),
                                    new JsonDecoder(),

                                    new ServerChannelInboundHandlerAdapter(threadPool)
                            );
                        }

                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            Channel channel = serverBootstrap.bind(9030).sync().channel();
            System.out.println("Server started");

            channel.closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workersGroup.shutdownGracefully();
            threadPool.shutdownNow();
        }


    }

}
