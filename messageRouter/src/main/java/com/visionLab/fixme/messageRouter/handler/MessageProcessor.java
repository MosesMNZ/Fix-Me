package com.visionLab.fixme.messageRouter.handler;

import java.util.Map;
import java.nio.channels.*;

import com.visionLab.fixme.coreModule.*;
import com.visionLab.fixme.coreModule.handler.BaseMessageHandler;


public class MessageProcessor extends BaseMessageHandler {

    private final Map<String, AsynchronousSocketChannel> routingTable;
    private final Map<String, String> failedMessages;

    public MessageProcessor(Map<String, AsynchronousSocketChannel> routingTable,
                            Map<String, String> failedMessages) {
        this.routingTable = routingTable;
        this.failedMessages = failedMessages;
    }

    @Override
    public void handle(AsynchronousSocketChannel clientChannel, String message) {
        System.out.println(MessageColors.ANSI_GREEN + "Processing message: " + message
            + MessageColors.ANSI_RESET);
        final String targetName = CoreModule.getFixValueByTag(message, FixTag.TARGET_NAME);
        final AsynchronousSocketChannel targetChannel = routingTable.get(targetName);
        if (targetChannel != null) {
            Utils.sendMessage(targetChannel, message);
            super.handle(clientChannel, message);
        } else {
            Utils.sendInternalMessage(clientChannel,
            MessageColors.ANSI_RED + "No connected client with such name: " 
            + targetName + ", will try later" + MessageColors.ANSI_RESET);
            failedMessages.put(targetName, message);
        }
    }
}
