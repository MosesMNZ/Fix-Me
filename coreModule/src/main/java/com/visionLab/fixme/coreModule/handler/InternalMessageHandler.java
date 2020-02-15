package com.visionLab.fixme.coreModule.handler;

import java.nio.channels.*;

import com.visionLab.fixme.coreModule.Utils;


public class InternalMessageHandler extends BaseMessageHandler {

    @Override
    public void handle(AsynchronousSocketChannel clientChannel, String message) {
        if (!message.startsWith(Utils.INTERNAL_MESSAGE)) {
            super.handle(clientChannel, message);
        }
    }
}
