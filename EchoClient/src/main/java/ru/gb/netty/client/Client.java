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


public class Client {

    public static void main(String[] args) throws  InterruptedException, IOException {
        new Client().run();
    }

    public void run() throws InterruptedException, IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        final DownloadFileRequestMessage requestMessage = new DownloadFileRequestMessage();
        final AuthClient authClient = new AuthClient();
        final RegClient regClient = new RegClient();

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
                                                    System.out.println("Попробуйте ещё раз!\n" +
                                                            "Авторизируйтесь ... \n" +
                                                            "Введите Ваш логин ... ");
                                                    authClient.setLog(bufferedReader.readLine());

                                                    System.out.println("Введите Ваш пароль ... ");
                                                    authClient.setPass(bufferedReader.readLine());

                                                    ctx.writeAndFlush(authClient);

                                            }
                                            if(msg instanceof Verification){
                                                System.out.println("Укажите путь к файлу который нужно загрузить...\n" +
                                                        "Пример ввода:\n" +
                                                        "C:\\Users\\budar\\IdeaProjects\\Netty\\1");
                                                requestMessage.setPath(bufferedReader.readLine());
                                                ctx.channel().writeAndFlush(requestMessage);
                                            }

                                            if (msg instanceof RegClient) {
                                                System.out.println(((RegClient) msg).getLog() + " регистрация прошла успешно!");
                                            }

                                            if (msg instanceof TextMessage) {
                                                System.out.println("Receive message " + ((TextMessage) msg).getText());

                                            }

                                            if (msg instanceof FileTransferMessage) {
                                                System.out.println("New incoming file download message");
                                                var message = (FileTransferMessage) msg;
                                                try (var accessFile = new RandomAccessFile("NewFile", "rw")) {
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

            System.out.println("Здравствуйте!\n" +
                    "Если Вы хотите авторизироватся введите Auth..\n" +
                    "Если у Вас ещё нет аккаунта введите Reg...");
            String answer = bufferedReader.readLine();
            if(answer.equals("Auth")) {
                System.out.println("Авторизируйтесь ... \n" +
                        "Введите Ваш логин ... ");
                authClient.setLog(bufferedReader.readLine());

                System.out.println("Введите Ваш пароль ... ");
                authClient.setPass(bufferedReader.readLine());

                channelFuture.channel().writeAndFlush(authClient);
                channelFuture.channel().closeFuture().sync();

            }if(answer.equals("Reg")){
                System.out.println("Зарегистрируйтесь ... \n " +
                        "Введите Ваш логин ... ");
                regClient.setLog(bufferedReader.readLine());

                System.out.println("Введите Ваш пароль ... ");
                regClient.setPass(bufferedReader.readLine());

                channelFuture.channel().writeAndFlush(regClient);
                channelFuture.channel().closeFuture().sync();

            }
        } finally {
            worker.shutdownGracefully();
        }
    }
}





