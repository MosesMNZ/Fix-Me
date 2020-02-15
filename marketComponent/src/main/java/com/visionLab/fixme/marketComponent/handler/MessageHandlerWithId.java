package com.visionLab.fixme.marketComponent.handler;

import java.nio.channels.AsynchronousSocketChannel;

import com.visionLab.fixme.coreModule.*;
import com.visionLab.fixme.coreModule.db.Database;
import com.visionLab.fixme.coreModule.handler.BaseMessageHandler;


public abstract class MessageHandlerWithId extends BaseMessageHandler {

    private final String id;
    private final String name;

    public MessageHandlerWithId(String id, String name) {
        this.id = id;
        this.name = name;
    }

    protected void rejectedMessage(AsynchronousSocketChannel clientChannel, String fixMessage, String message) {
        sendMessage(clientChannel, fixMessage, message, Result.Rejected);
    }

    protected void executedMessage(AsynchronousSocketChannel clientChannel, String fixMessage, String message) {
        sendMessage(clientChannel, fixMessage, message, Result.Executed);
    }

    private void sendMessage(AsynchronousSocketChannel clientChannel, String fixMessage, String message, Result result) {
        final String targetName = CoreModule.getFixValueByTag(fixMessage, FixTag.SOURCE_NAME);
        if (isInsertMessagesToDb()) {
            Database.insert(
                    name,
                    targetName,
                    CoreModule.getFixValueByTag(fixMessage, FixTag.TYPE),
                    CoreModule.getFixValueByTag(fixMessage, FixTag.INSTRUMENT),
                    CoreModule.getFixValueByTag(fixMessage, FixTag.PRICE),
                    CoreModule.getFixValueByTag(fixMessage, FixTag.QUANTITY),
                    result.toString(),
                    message);
            Database.selectAll();
        }
        Utils.sendMessage(clientChannel, CoreModule.resultFixMessage(message, id, name, targetName, result));
    }

    protected boolean isInsertMessagesToDb() {
        return false;
    }
}
