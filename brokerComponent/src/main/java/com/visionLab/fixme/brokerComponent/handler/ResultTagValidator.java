package com.visionLab.fixme.brokerComponent.handler;

import java.nio.channels.*;

import com.visionLab.fixme.coreModule.*;
import com.visionLab.fixme.coreModule.exception.WrongFixTagException;
import com.visionLab.fixme.coreModule.handler.BaseMessageHandler;


public class ResultTagValidator extends BaseMessageHandler {

    @Override
    public void handle(AsynchronousSocketChannel clientChannel, String message) {
        final String result;
        try {
            result = CoreModule.getFixValueByTag(message, FixTag.RESULT);
        } catch (WrongFixTagException ex) {
            System.out.println(MessageColors.ANSI_RED
                + ex.getMessage() + MessageColors.ANSI_RESET
            );
            return;
        }
        if (Result.is(result)) {
            super.handle(clientChannel, message);
        } else {
            System.out.println(MessageColors.ANSI_RED
                + "Wrong result type in message: " + message + MessageColors.ANSI_RESET
            );
        }
    }
}
