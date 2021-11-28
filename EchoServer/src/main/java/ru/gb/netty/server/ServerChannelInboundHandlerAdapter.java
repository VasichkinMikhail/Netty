package ru.gb.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.netty.message.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Executor;

public class ServerChannelInboundHandlerAdapter extends SimpleChannelInboundHandler<Message> {
    private static final String FILE_NAME = "C:\\Users\\budar\\IdeaProjects\\Netty\\test1.json";
    private static final int BUFFER_SIZE = 1024 *64;
    private final Executor executor;

    public ServerChannelInboundHandlerAdapter(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Chanel is registered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Chanel is unregistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Chanel active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Chanel un active");
    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws IOException {
        if (msg instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) msg;
            System.out.println("Incoming text message from client :" + textMessage.getText());
            ctx.writeAndFlush(msg);
        }
        if (msg instanceof DateMessage) {
            DateMessage dateMessage = (DateMessage) msg;
            System.out.println("Incoming date message from client :" + dateMessage.getData());
            ctx.writeAndFlush(msg);
//        }
//        if(msg instanceof DownloadFileRequestMessage){
//            var message = (DownloadFileRequestMessage) msg;
//            try(RandomAccessFile accessFile = new RandomAccessFile(message.getPath(),"r")){
//                final FileMessage fileMessage = new FileMessage();
//                byte[] content = new byte[(int)accessFile.length()];
//                accessFile.read(content);
//                fileMessage.setContent(content);
//                ctx.writeAndFlush(fileMessage);
        }
        if (msg instanceof RequestFileMessage) {
            executor.execute(() -> {

                try (var randomAccessFile = new RandomAccessFile(FILE_NAME, "r")) {
                    final long fileLength = randomAccessFile.length();
                    do {
                        var position = randomAccessFile.getFilePointer();
                        final long availableBytes = fileLength - position;
                        byte[] bytes;

                        if (availableBytes >= BUFFER_SIZE) {
                            bytes = new byte[BUFFER_SIZE];
                        } else {
                            bytes = new byte[(int) availableBytes];
                        }
                        randomAccessFile.read(bytes);
                        final FileTransferMessage message = new FileTransferMessage();
                        message.setContent(bytes);
                        message.setStartPosition(position);
                        ctx.writeAndFlush(message);


                    } while (randomAccessFile.getFilePointer() < fileLength);
                    ctx.writeAndFlush(new EndFileTransferMessage());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }




    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Catch cause" + cause.getMessage());
        ctx.close();
    }
}
