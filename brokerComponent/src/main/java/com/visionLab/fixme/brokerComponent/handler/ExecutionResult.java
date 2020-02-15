package com.visionLab.fixme.brokerComponent.handler;

import java.nio.channels.*;

import com.visionLab.fixme.coreModule.*;
import com.visionLab.fixme.coreModule.handler.BaseMessageHandler;


public class ExecutionResult extends BaseMessageHandler {

    @Override
    public void handle(AsynchronousSocketChannel clientChannel, String message) {
        final String result = CoreModule.getFixValueByTag(message, FixTag.RESULT);
        final String resultMessage = CoreModule.getFixValueByTag(message, FixTag.MESSAGE);
        System.out.println(MessageColors.ANSI_GREEN 
            + "Operation result: " + result + " - " + resultMessage + MessageColors.ANSI_RESET
        );
        super.handle(clientChannel, message);
    }
}
