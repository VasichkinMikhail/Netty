package ru.gb.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import ru.gb.netty.database.AuthLoginPassword;
import ru.gb.netty.database.DataBase;
import ru.gb.netty.handler.JsonDecoder;
import ru.gb.netty.handler.JsonEncoder;
import ru.gb.netty.message.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.SQLException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws InterruptedException, SQLException {
        new Client().run();

    }

    public void run() throws InterruptedException, SQLException {
        NioEventLoopGroup worker = new NioEventLoopGroup(1);
        try{
            Bootstrap bootstrap = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024 *1024, 0, 3, 0,3 ),
                                    new LengthFieldPrepender(3),
                                    new JsonEncoder(),
                                    new JsonDecoder(),
                                    new SimpleChannelInboundHandler<Message>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws IOException {
                                            if (msg instanceof TextMessage) {
                                                System.out.println("Receive message " + ((TextMessage) msg).getText());
                                            }
                                            if (msg instanceof FileTransferMessage) {
                                                System.out.println("New incoming file download message");
                                                var message = (FileTransferMessage) msg;
                                                try (final RandomAccessFile accessFile = new RandomAccessFile("3", "rw")) {
                                                    accessFile.seek(message.getStartPosition());
                                                    accessFile.write(message.getContent());
                                                }
                                            }if(msg instanceof EndFileTransferMessage){
                                                ctx.close();
                                            }
                                        }
                                    }
                            );
                        }
                    })

                    .option(ChannelOption.SO_KEEPALIVE, true);
                   System.out.println("Client started");

                   ChannelFuture channelFuture = bootstrap.connect("localhost", 9030).sync();
                   final DownloadFileRequestMessage requestMessage = new DownloadFileRequestMessage();
                   requestMessage.setPath("C:\\Users\\budar\\IdeaProjects\\Netty\\1");
                   channelFuture.channel().writeAndFlush(requestMessage);
                   channelFuture.channel().closeFuture().sync();

        }finally{
            worker.shutdownGracefully();
        }
        }

}


