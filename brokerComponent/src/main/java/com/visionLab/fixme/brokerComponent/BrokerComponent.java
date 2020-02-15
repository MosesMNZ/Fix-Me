package com.visionLab.fixme.brokerComponent;

import java.util.Scanner;
import java.util.concurrent.*;


import com.visionLab.fixme.coreModule.*;
import com.visionLab.fixme.brokerComponent.handler.*;
import com.visionLab.fixme.coreModule.handler.MessageHandler;
import com.visionLab.fixme.coreModule.exception.UserInputValidationException;



public class BrokerComponent extends Client {

    private BrokerComponent(String name) {
        super(CoreModule.BROKER_PORT, "B" + name);
    }

    private void run() {
        try {
            readFromSocket();

            final Scanner scanner = new Scanner(System.in);
            System.out.println(MessageColors.ANSI_CYAN + "------------------------------------------------------------" + MessageColors.ANSI_RESET);
            System.out.println(MessageColors.ANSI_CYAN + "| " + MessageColors.ANSI_RESET + MessageColors.ANSI_CYAN_BACKGROUND + "Please enter all the following options in one line:     " + MessageColors.ANSI_RESET + MessageColors.ANSI_CYAN + " |");
            System.out.println(MessageColors.ANSI_CYAN + "| " + MessageColors.ANSI_RESET + MessageColors.ANSI_CYAN_BACKGROUND + "                  1. Market Name                        " + MessageColors.ANSI_RESET + MessageColors.ANSI_CYAN + " |");
            System.out.println(MessageColors.ANSI_CYAN + "| " + MessageColors.ANSI_RESET + MessageColors.ANSI_CYAN_BACKGROUND + "                  2. Buy or Sell                        " + MessageColors.ANSI_RESET + MessageColors.ANSI_CYAN + " |");
            System.out.println(MessageColors.ANSI_CYAN + "| " + MessageColors.ANSI_RESET + MessageColors.ANSI_CYAN_BACKGROUND + "                  3. Product Name                       " + MessageColors.ANSI_RESET + MessageColors.ANSI_CYAN + " |");
            System.out.println(MessageColors.ANSI_CYAN + "| " + MessageColors.ANSI_RESET + MessageColors.ANSI_CYAN_BACKGROUND + "                  4. Quantity                           " + MessageColors.ANSI_RESET + MessageColors.ANSI_CYAN + " |");
            System.out.println(MessageColors.ANSI_CYAN + "| " + MessageColors.ANSI_RESET + MessageColors.ANSI_CYAN_BACKGROUND + "                  5. Price in Rand                      " + MessageColors.ANSI_RESET + MessageColors.ANSI_CYAN + " |");
            System.out.println(MessageColors.ANSI_CYAN + "------------------------------------------------------------" + MessageColors.ANSI_RESET);

            while (true) {
                try {
                    final String message = CoreModule.userInputToFixMessage(scanner.nextLine(), getId(), getName());
                    final Future<Integer> result = Utils.sendMessage(getSocketChannel(), message);
                    result.get();
                } catch (UserInputValidationException e) {
                    System.out.println(MessageColors.ANSI_RED
                        + e.getMessage() + MessageColors.ANSI_RESET
                    );
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected MessageHandler getMessageHandler() {
        final MessageHandler messageHandler = super.getMessageHandler();
        final MessageHandler resultTag = new ResultTagValidator();
        final MessageHandler executionResult = new ExecutionResult();
        messageHandler.setNext(resultTag);
        resultTag.setNext(executionResult);
        return messageHandler;
    }

    public static void main(String[] args) {
        new BrokerComponent(Utils.getClientName(args)).run();
    }
}
