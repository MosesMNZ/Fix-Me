package com.visionLab.fixme.messageRouter;


import java.nio.channels.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.visionLab.fixme.coreModule.*;
import com.visionLab.fixme.coreModule.handler.*;
import com.visionLab.fixme.messageRouter.handler.MessageProcessor;

public class MessageRouter {

    private final AtomicInteger id = new AtomicInteger(CoreModule.INITIAL_ID);
    private final Map<String, String> unableToSendMessages = new ConcurrentHashMap<>();
    private final Map<String, AsynchronousSocketChannel> routingTable = new ConcurrentHashMap<>();

    private void run() {
        System.out.println(MessageColors.ANSI_GREEN + 
            "Message Router is " + MessageColors.ANSI_CYAN_BACKGROUND + "Turned ON" + MessageColors.ANSI_RESET);
        try {
            final MessageHandler messageHandler = getMessageHandler();

            final AsynchronousServerSocketChannel brokerComponentConnectionsListener = AsynchronousServerSocketChannel
                    .open().bind(new InetSocketAddress(CoreModule.LOCALHOST, CoreModule.BROKER_PORT));
            brokerComponentConnectionsListener.accept(null,
                    new ClientCompletionHandler(brokerComponentConnectionsListener, routingTable, id, messageHandler));

            final AsynchronousServerSocketChannel marketComponentConnectionsListener = AsynchronousServerSocketChannel
                    .open().bind(new InetSocketAddress(CoreModule.LOCALHOST, CoreModule.MARKET_PORT));
            marketComponentConnectionsListener.accept(null,
                    new ClientCompletionHandler(marketComponentConnectionsListener, routingTable, id, messageHandler));
        } catch (IOException e) {
            System.out.println(MessageColors.ANSI_RED + "Unable to open socket: " + e);
        }
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            tryToSendFailedMessages();
        }
    }

    private MessageHandler getMessageHandler() {
        final MessageHandler messageHandler = new InternalMessageHandler();
        final MessageHandler mandatoryTagsValidator = new MandatoryTagsValidator();
        final MessageHandler checksumValidator = new ChecksumValidator();
        final MessageHandler messageParser = new MessageProcessor(routingTable, unableToSendMessages);
        messageHandler.setNext(mandatoryTagsValidator);
        mandatoryTagsValidator.setNext(checksumValidator);
        checksumValidator.setNext(messageParser);
        return messageHandler;
    }

    private void tryToSendFailedMessages() {
        if (!unableToSendMessages.isEmpty()) {
            System.out.println(MessageColors.ANSI_RED + "Trying to send failed messages...");
            unableToSendMessages.keySet().removeIf(targetName -> {
                final AsynchronousSocketChannel targetChannel = routingTable.get(targetName);
                if (targetChannel != null) {
                    System.out.println(MessageColors.ANSI_GREEN + "Found message to resend " 
                        + targetName + ", sending message" + MessageColors.ANSI_RESET);
                    Utils.sendMessage(targetChannel, unableToSendMessages.get(targetName));
                    return true;
                }
                return false;
            });
        }
    }

    public static void main(String[] args) {
        new MessageRouter().run();
    }
}
