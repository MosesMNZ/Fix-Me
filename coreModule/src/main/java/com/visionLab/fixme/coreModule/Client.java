package com.visionLab.fixme.coreModule;


import java.nio.*;
import java.nio.channels.*;
import java.io.IOException;
import java.util.concurrent.*;
import java.net.InetSocketAddress;

import com.visionLab.fixme.coreModule.handler.*;



public abstract class Client {

    private final ByteBuffer buffer = ByteBuffer.allocate(CoreModule.DEFAULT_BUFFER_SIZE);
    private final int port;
    private final String name;

    private AsynchronousSocketChannel socketChannel;
    private String id = "000000";

    public Client(int port, String name) {
        this.port = port;
        this.name = name;
    }

    protected AsynchronousSocketChannel getSocketChannel() {
        if (socketChannel == null) {
            socketChannel = connectToMessageRouter();
            Utils.sendMessage(socketChannel, name);
            id = Utils.readMessage(socketChannel, buffer);
            final String color = ((id.equals("000001")) ? MessageColors.ANSI_BLUE : MessageColors.ANSI_YELLOW);
            System.out.println(color + name + " ID: " + id + MessageColors.ANSI_RESET);
            return socketChannel;
        }
        return socketChannel;
    }

    private AsynchronousSocketChannel connectToMessageRouter() {
        final AsynchronousSocketChannel socketChannel;
        try {
            socketChannel = AsynchronousSocketChannel.open();
            final Future future = socketChannel.connect(new InetSocketAddress(CoreModule.LOCALHOST, port));
            future.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            System.out.println(MessageColors.ANSI_RED 
                + "Could not connect to Message Router, reconnecting..."
                + MessageColors.ANSI_RESET
            );
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            return connectToMessageRouter();
        }
        return socketChannel;
    }

    private void invalidateConnection() {
        socketChannel = null;
    }

    protected String getId() {
        return id;
    }

    protected String getName() {
        return name;
    }

    private void onSuccessRead(String message) {
        getMessageHandler().handle(getSocketChannel(), message);
    }

    protected MessageHandler getMessageHandler() {
        final MessageHandler messageHandler = new InternalMessageHandler();
        final MessageHandler mandatoryTagsValidator = new MandatoryTagsValidator();
        final MessageHandler checksumValidator = new ChecksumValidator();
        messageHandler.setNext(mandatoryTagsValidator);
        mandatoryTagsValidator.setNext(checksumValidator);
        return messageHandler;
    }

    protected void readFromSocket() {
        getSocketChannel().read(buffer, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                final String message = Utils.read(result, buffer);
                if (!Utils.EMPTY_MESSAGE.equals(message)) {
                    onSuccessRead(message);
                    getSocketChannel().read(buffer, null, this);
                } else {
                    reconnect();
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                reconnect();
            }

            private void reconnect() {
                System.out.println(MessageColors.ANSI_RED
                    + "The Message Router is disconnected!"
                    + MessageColors.ANSI_RESET
                );
                invalidateConnection();
                getSocketChannel().read(buffer, null, this);
            }
        });
    }

   

    
}
