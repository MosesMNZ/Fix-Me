package com.visionLab.fixme.coreModule.handler;

import java.nio.channels.*;

import com.visionLab.fixme.coreModule.*;
import com.visionLab.fixme.coreModule.exception.WrongFixTagException;


public class MandatoryTagsValidator extends BaseMessageHandler {

    @Override
    public void handle(AsynchronousSocketChannel clientChannel, String message) {
        try {
            final String sourceId = CoreModule.getFixValueByTag(message, FixTag.ID);
            CoreModule.getFixValueByTag(message, FixTag.SOURCE_NAME);
            CoreModule.getFixValueByTag(message, FixTag.TARGET_NAME);
            final String checksum = CoreModule.getFixValueByTag(message, FixTag.CHECKSUM);

            Integer.parseInt(sourceId);
            Integer.parseInt(checksum);
            super.handle(clientChannel, message);
        } catch (WrongFixTagException ex) {
            Utils.sendInternalMessage(clientChannel, ex.getMessage());
        } catch (NumberFormatException ex) {
            Utils.sendInternalMessage(clientChannel, MessageColors.ANSI_RED + "SOURCE_ID, CHECKSUM Tags should be numbers: " + message);
        }
    }
}
