package com.visionLab.fixme.marketComponent.handler;

import java.util.Map;
import java.nio.channels.AsynchronousSocketChannel;

import com.visionLab.fixme.coreModule.*;
import com.visionLab.fixme.marketComponent.MarketComponent;


public class MessageExecutor extends MessageHandlerWithId {

    private final Map<String, Integer> instruments;

    public MessageExecutor(String clientId, String name, Map<String, Integer> instruments) {
        super(clientId, name);
        this.instruments = instruments;
    }

    @Override
    public void handle(AsynchronousSocketChannel clientChannel, String message) {
        final String instrument = CoreModule.getFixValueByTag(message, FixTag.INSTRUMENT);
        if (instruments.containsKey(instrument)) {
            final int quantity = Integer.parseInt(CoreModule.getFixValueByTag(message, FixTag.QUANTITY));
            final int marketComponentQuantity = instruments.get(instrument);
            final String type = CoreModule.getFixValueByTag(message, FixTag.TYPE);
            if (type.equals(MessageType.Buy.toString())) {
                if (marketComponentQuantity < quantity) {
                    rejectedMessage(clientChannel, message,
                        MessageColors.ANSI_RED + "Not enough instruments"
                        + MessageColors.ANSI_RESET);
                    return;
                } else {
                    instruments.put(instrument, marketComponentQuantity - quantity);
                }
            } else {
                instruments.put(instrument, marketComponentQuantity + quantity);
            }
             // calling the list of products methods
            MarketComponent.listOfProducts(instruments);

            executedMessage(clientChannel, message, 
                MessageColors.ANSI_GREEN + "OK"
                + MessageColors.ANSI_RESET
            );
        } else {
            rejectedMessage(clientChannel, message, MessageColors.ANSI_RED 
                + instrument + " instrument is not traded on the marketComponent"
                + MessageColors.ANSI_RESET
            );
        }
    }

    @Override
    protected boolean isInsertMessagesToDb() {
        return true;
    }
}
