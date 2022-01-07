package ru.gb.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import ru.gb.netty.authDatabase.*;
import ru.gb.netty.message.*;

import java.io.*;
import java.util.concurrent.Executor;

public class ServerChannelHandlerAdapter extends SimpleChannelInboundHandler<Message> {

    private static final int BUFFER_SIZE = 1024 * 64;
    private final Executor executor;
    DataBase dataBase = new DataBase();


    public ServerChannelHandlerAdapter(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx){
        System.out.println("Channel is registered");
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx){
        System.out.println("Chanel is unregistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Channel active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        System.out.println("Channel inactive");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
             if(msg instanceof AuthClient) {
                 var message = (AuthClient) msg;
                 if (message.getLog().equals(dataBase.getClients(message))) {
                     ctx.writeAndFlush(new Verification());
                 } else {
                     ctx.writeAndFlush(new AuthClient());

                 }
             }

             if (msg instanceof RegClient){
                 var message = (RegClient) msg;
                 if(message.getLog().equals(dataBase.clientCheck(message))){
                     System.out.println("Такой login существует");
                     ctx.writeAndFlush(new RegClient());
                 }else {
                     dataBase.getNewClients(message);
                     System.out.println("Регистрация прошла успешно");
                     ctx.writeAndFlush(new AuthClient());
                 }

             }

            if (msg instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) msg;
                System.out.println("Incoming text message from client :" + textMessage.getText());
                ctx.writeAndFlush(msg);
            }
            if (msg instanceof DownloadFileRequestMessage) {

                executor.execute(() -> {
                    var message = (DownloadFileRequestMessage) msg;
                    try (final FileWriter writer = new FileWriter("fileList.txt", true)) {
                        writer.write(message.getFileName()+"\n");
                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try (var randomAccessFile = new RandomAccessFile(message.getPath()+"\\" + message.getFileName(), "r")) {
                        long fileLength = randomAccessFile.length();
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
                            final FileTransferMessage filemessage = new FileTransferMessage();
                            filemessage.setFileName(message.getFileName());
                            filemessage.setContent(bytes);
                            filemessage.setStartPosition(position);
                            ctx.writeAndFlush(filemessage).sync();


                        } while (randomAccessFile.getFilePointer() < fileLength);
                        ctx.writeAndFlush(new EndFileTransferMessage());
                        ctx.close();



                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        if (msg instanceof AploadFileRequestMessage) {

            executor.execute(() -> {
                var message = (AploadFileRequestMessage) msg;

                try (var randomAccessFile = new RandomAccessFile("C:\\Users\\budar\\IdeaProjects\\Netty\\"+ message.getFileName(), "r")) {
                    long fileLength = randomAccessFile.length();
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
                        final ClientFileTransferMessage transferMessage = new ClientFileTransferMessage();
                        transferMessage.setFileName(message.getFileName());
                        transferMessage.setPath(message.getPath());
                        transferMessage.setContent(bytes);
                        transferMessage.setStartPosition(position);
                        ctx.writeAndFlush(transferMessage).sync();


                    } while (randomAccessFile.getFilePointer() < fileLength);
                    ctx.writeAndFlush(new EndFileTransferMessage());
                    ctx.close();

                } catch (IOException | InterruptedException e) {
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.out.println("Catch cause" + cause.getMessage());
        ctx.close();
    }
}
