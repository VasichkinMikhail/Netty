package ru.gb.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import ru.gb.netty.handler.JsonDecoder;
import ru.gb.netty.handler.JsonEncoder;
import ru.gb.netty.lite.*;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        new Client().run();

    }

    public void run() throws InterruptedException {
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
                                    new JsonEncoder(),
                                    new JsonDecoder(),
                                    new SimpleChannelInboundHandler<Message>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws IOException {
                                            if (msg instanceof FileMessage) {
                                                var message = (FileMessage) msg;
                                                try (final RandomAccessFile accessFile = new RandomAccessFile("Text", "rw")) {
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

            Channel channel = bootstrap.connect("localhost", 9020).sync().channel();
            while (true) {

                final DownloadFileRequestMessage message = new DownloadFileRequestMessage();
                message.setPath("C:\\Users\\budar\\IdeaProjects\\Netty\\test1.json");
                channel.writeAndFlush(message);

                DateMessage dateMessage = new DateMessage();
                dateMessage.setData(new Date());
                channel.writeAndFlush(dateMessage);

                TextMessage textmessage = new TextMessage();
                textmessage.setText("New text from client");
                channel.writeAndFlush(textmessage);


                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            } finally{
                worker.shutdownGracefully();
            }
        }
    }



