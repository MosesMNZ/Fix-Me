package com.visionLab.fixme.marketComponent.handler;

import java.nio.channels.AsynchronousSocketChannel;

import com.visionLab.fixme.coreModule.*;
import com.visionLab.fixme.coreModule.exception.WrongFixTagException;


public class MarketTagsValidator extends MessageHandlerWithId {

    public MarketTagsValidator(String id, String name) {
        super(id, name);
    }

    @Override
    public void handle(AsynchronousSocketChannel clientChannel, String message) {
        try {
            CoreModule.getFixValueByTag(message, FixTag.INSTRUMENT);
            final int price = Integer.parseInt(CoreModule.getFixValueByTag(message, FixTag.PRICE));
            final int quantity = Integer.parseInt(CoreModule.getFixValueByTag(message, FixTag.QUANTITY));
            if (quantity <= 0 || quantity > 10000) {
                rejectedMessage(clientChannel, message, MessageColors.ANSI_RED + "Wrong quantity(1-10k)");
                return;
            } else if (price <= 0 || price > 10000) {
                rejectedMessage(clientChannel, message, MessageColors.ANSI_RED + "Wrong price(1-10k");
                return;
            }

            final String type = CoreModule.getFixValueByTag(message, FixTag.TYPE);
            if (MessageType.is(type)) {
                super.handle(clientChannel, message);
            } else {
                rejectedMessage(clientChannel, message, MessageColors.ANSI_RED + "Wrong operation type");
            }
        } catch (WrongFixTagException ex) {
            rejectedMessage(clientChannel, message, MessageColors.ANSI_RED + "Wrong fix tags");
        } catch (NumberFormatException ex) {
            rejectedMessage(clientChannel, message, MessageColors.ANSI_RED + "Wrong value type");
        }
    }
}
