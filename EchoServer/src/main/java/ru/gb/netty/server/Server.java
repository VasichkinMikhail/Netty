package ru.gb.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import ru.gb.netty.authDatabase.*;
import ru.gb.netty.handler.JsonDecoder;
import ru.gb.netty.handler.JsonEncoder;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
                                    new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),//погуглим
                                    new LengthFieldPrepender(3),
                                    new JsonEncoder(),
                                    new JsonDecoder(),
                                    new ServerChannelHandlerAdapter(threadPool)

                            );
                        }

                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            Channel channel = serverBootstrap.bind(9010).sync().channel();
                System.out.println("Server started");
                new DataBase().connect();


                channel.closeFuture().sync();
            } finally {
                bossGroup.shutdownGracefully();
                workersGroup.shutdownGracefully();
                threadPool.shutdownNow();
            }
        }

    }

