package com.visionLab.fixme.coreModule.handler;

import java.nio.channels.AsynchronousSocketChannel;

import com.visionLab.fixme.coreModule.*;



public class ChecksumValidator extends BaseMessageHandler {

    @Override
    public void handle(AsynchronousSocketChannel clientChannel, String message) {
        final String calculatedChecksum = CoreModule.calculateChecksum(CoreModule.getMessageWithoutChecksum(message));
        final String messageChecksum = CoreModule.getFixValueByTag(message, FixTag.CHECKSUM);
        final boolean isValidChecksum = calculatedChecksum.equals(messageChecksum);
        if (isValidChecksum) {
            super.handle(clientChannel, message);
        } else {
            Utils.sendInternalMessage(clientChannel, "Checksum is incorrect: " + message);
        }
    }
}
