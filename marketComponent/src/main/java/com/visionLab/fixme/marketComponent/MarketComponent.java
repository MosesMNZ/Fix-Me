package com.visionLab.fixme.marketComponent;

import java.util.Map;
import java.util.Formatter;

import com.visionLab.fixme.coreModule.*;
import com.visionLab.fixme.marketComponent.handler.*;
import com.visionLab.fixme.coreModule.handler.MessageHandler;



public class MarketComponent extends Client {

    private final Map<String, Integer> instruments;

    private MarketComponent(String name) {
        super(CoreModule.MARKET_PORT, "M" + name);
        instruments = Utils.getRandomInstruments();
    }

    private void run() {
        listOfProducts(instruments);

        readFromSocket();
        
        while (true) ;
    }

    public static void listOfProducts(Map<String, Integer> instruments) {
        System.out.println(MessageColors.ANSI_CYAN + "-----------------------------------" + MessageColors.ANSI_RESET);
        System.out.println("| " + MessageColors.ANSI_CYAN_BACKGROUND + " List of Products  ="  + "  Quantity " + MessageColors.ANSI_RESET + " |");
        System.out.println(MessageColors.ANSI_CYAN + "-----------------------------------" + MessageColors.ANSI_RESET);


        for(Map.Entry<String, Integer> instrument : instruments.entrySet()) {
            System.out.printf("|  %-26s = %5d  %5s%n", MessageColors.ANSI_GREEN + instrument.getKey() + MessageColors.ANSI_RESET, instrument.getValue(), "|");
            System.out.println(MessageColors.ANSI_CYAN + "-----------------------------------" + MessageColors.ANSI_RESET);
        }
    }

    @Override
    protected MessageHandler getMessageHandler() {
        final MessageHandler messageHandler = super.getMessageHandler();
        final MessageHandler tagsValidator = new MarketTagsValidator(getId(), getName());
        final MessageHandler messageExecutor = new MessageExecutor(getId(), getName(), instruments);
        messageHandler.setNext(tagsValidator);
        tagsValidator.setNext(messageExecutor);
        return messageHandler;
    }

    public static void main(String[] args) {
        new MarketComponent(Utils.getClientName(args)).run();
    }
}
