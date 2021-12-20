package ru.gb.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import ru.gb.netty.authDatabase.*;
import ru.gb.netty.handler.JsonDecoder;
import ru.gb.netty.handler.JsonEncoder;
import ru.gb.netty.message.*;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;

public class Client {


    public static void main(String[] args) throws SQLException, InterruptedException, IOException {
        new Client().run();
    }


    public void run() throws InterruptedException, SQLException, IOException {

        NioEventLoopGroup worker = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                                    new LengthFieldPrepender(3),
                                    new JsonEncoder(),
                                    new JsonDecoder(),
                                    new SimpleChannelInboundHandler<Message>() {

                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
                                            if (msg instanceof AuthClient) {
                                                System.out.println(((AuthClient) msg).getName() + " авторизация прошла успешно!");
                                            }
                                            if (msg instanceof RegClient) {
                                                System.out.println(((RegClient) msg).getName() + " регистрация прошла успешно!");

                                            }
                                            if (msg instanceof AuthService) {
                                                var message = (AuthService) msg;
                                                System.out.println("Попробуем еще раз ...");
                                                message.authorisation();
                                            }


                                            if (msg instanceof TextMessage) {
                                                System.out.println("Receive message " + ((TextMessage) msg).getText());
                                            }

                                            if (msg instanceof FileTransferMessage) {
                                                System.out.println("New incoming file download message");
                                                var message = (FileTransferMessage) msg;
                                                try (var accessFile = new RandomAccessFile("file", "rw")) {
                                                    accessFile.seek(message.getStartPosition());
                                                    accessFile.write(message.getContent());
                                                }

                                                if (msg instanceof EndFileTransferMessage) {
                                                    ctx.close();
                                                }
                                            }
                                        }
                                    }
                            );
                        }

                    })

                    .option(ChannelOption.SO_KEEPALIVE, true);
            System.out.println("Client started");
            ChannelFuture channelFuture = bootstrap.connect("localhost", 9010).sync();

            final Scanner scanner = new Scanner(System.in);
            final AuthService service = new AuthService();
            System.out.println("Здравствуйте\n" +
                    "Если Вы хотите авторизоваться введите Aug\n" +
                    "Если Вы хотите зарегистрироваться введите Reg");
            String answer = scanner.nextLine();
            if (answer.equals("Aug")) {
                channelFuture.channel().writeAndFlush(service.authorisation());
            }
            else if (answer.equals("Reg")){
                channelFuture.channel().writeAndFlush(service.registration());
            }else {
                System.out.println("Не корректный ввод!");
                channelFuture.channel().closeFuture().sync();
            }






            TextMessage textMessage = new TextMessage();
            textMessage.setText("New incoming message");
            channelFuture.channel().writeAndFlush(textMessage);


            final DownloadFileRequestMessage requestMessage = new DownloadFileRequestMessage();
            requestMessage.setPath("C:\\Users\\budar\\IdeaProjects\\Netty\\1");
            channelFuture.channel().writeAndFlush(requestMessage);
            channelFuture.channel().closeFuture().sync();


        } finally {
            worker.shutdownGracefully();

        }
    }
}





