package ru.gb.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import ru.gb.netty.database.AuthLoginPassword;
import ru.gb.netty.handler.JsonDecoder;
import ru.gb.netty.handler.JsonEncoder;
import ru.gb.netty.message.*;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        final AuthLoginPassword authLoginPassword = new AuthLoginPassword();
        authLoginPassword.authService();
        new Client().run();

    }

    public void run() throws InterruptedException {
        NioEventLoopGroup worker = new NioEventLoopGroup(1);
        try{
            Bootstrap bootstrap = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(512, 0, 2, 0, 2),
                                    new LengthFieldPrepender(2),
                                    new JsonEncoder(),
                                    new JsonDecoder(),
                                    new SimpleChannelInboundHandler<Message>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws IOException {
                                            if (msg instanceof FileMessage) {
                                                var message = (FileMessage) msg;
                                                try (final RandomAccessFile accessFile = new RandomAccessFile("Text2", "rw")) {
                                                    accessFile.write(message.getContent());
                                                }
                                                ctx.close();
                                            }
                                        }
                                    }
                            );
                        }
                    })

                    .option(ChannelOption.SO_KEEPALIVE, true);

//            Channel channel = bootstrap.connect("localhost", 9020).sync().channel();
               while (true) {

                ChannelFuture channelFuture = bootstrap.connect("localhost", 9020).sync();
                channelFuture.channel().writeAndFlush(new RequestFileMessage());
                channelFuture.channel().closeFuture().sync();
//            while (true) {
//
//                final DownloadFileRequestMessage message = new DownloadFileRequestMessage();
//                message.setPath("C:\\Users\\budar\\IdeaProjects\\Netty\\1");
//                channel.writeAndFlush(message);
//
//                DateMessage dateMessage = new DateMessage();
//                dateMessage.setData(new Date());
//                channel.writeAndFlush(dateMessage);
//
//                TextMessage textmessage = new TextMessage();
//                textmessage.setText("New text from client");
//                channel.writeAndFlush(textmessage);


                //               Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();

            }
        }finally{
            worker.shutdownGracefully();
        }
        }

}


