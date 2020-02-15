package com.visionLab.fixme.messageRouter;

import java.nio.channels.*;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.visionLab.fixme.coreModule.*;
import com.visionLab.fixme.coreModule.handler.MessageHandler;



class ClientCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {

    private static final int EXECUTOR_THREADS = 5;

    private final ExecutorService executor = Executors.newFixedThreadPool(EXECUTOR_THREADS);
    private final AsynchronousServerSocketChannel listener;
    private final Map<String, AsynchronousSocketChannel> routingTable;
    private final AtomicInteger id;
    private final MessageHandler messageHandler;

    private String clientName = "client";

    ClientCompletionHandler(AsynchronousServerSocketChannel listener, Map<String, AsynchronousSocketChannel> routingTable,
                            AtomicInteger id, MessageHandler messageHandler) {
        this.listener = listener;
        this.routingTable = routingTable;
        this.id = id;
        this.messageHandler = messageHandler;
    }

    @Override
    public void completed(AsynchronousSocketChannel channel, Object attachment) {
        listener.accept(null, this);
        final ByteBuffer buffer = ByteBuffer.allocate(CoreModule.DEFAULT_BUFFER_SIZE);
        clientName = Utils.readMessage(channel, buffer);

        sendClientId(channel, getNextId());

        while (true) {
            final String message = Utils.readMessage(channel, buffer);
            if (Utils.EMPTY_MESSAGE.equals(message)) {
                break;
            }
            executor.execute(() -> messageHandler.handle(channel, message));
        }
        endConnection();
    }

    @Override
    public void failed(Throwable exc, Object attachment) {
        endConnection();
    }

    private void sendClientId(AsynchronousSocketChannel channel, String currentId) {
        System.out.println();
        System.out.println(MessageColors.ANSI_PURPLE +
            clientName + " " + clientName + " connected, ID: "
            + currentId + MessageColors.ANSI_RESET);
        Utils.sendMessage(channel, currentId);
        routingTable.put(clientName, channel);
        printRoutingTable();
    }

    private void endConnection() {
        routingTable.remove(clientName);
        System.out.println();
        System.out.println(MessageColors.ANSI_PURPLE + clientName + " " + clientName 
            + " connection ended" + MessageColors.ANSI_RESET);
        printRoutingTable();
    }

    private void printRoutingTable() {
        System.out.println(MessageColors.ANSI_GREEN +"Routing table: " + routingTable.keySet().toString()
        + MessageColors.ANSI_RESET);
    }

    private String getNextId() {
        return String.format(CoreModule.ID_FORMAT, id.getAndIncrement());
    }
}
