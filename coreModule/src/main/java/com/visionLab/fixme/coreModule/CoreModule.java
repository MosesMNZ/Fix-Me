package com.visionLab.fixme.coreModule;

import java.util.regex.Pattern;

import com.visionLab.fixme.coreModule.exception.*;


public class CoreModule {

    public static final String LOCALHOST = "127.0.0.1";
    public static final int BROKER_PORT = 5000;
    public static final int MARKET_PORT = 5001;
    public static final int INITIAL_ID = 1;
    public static final int DEFAULT_BUFFER_SIZE = 4096;
    public static final String ID_FORMAT = "%06d";
    public static final String USER_MESSAGE_FORMAT = "'MARKET_ID  Buy_OR_Sell INSTRUMENT_NAME QUANTITY PRICE'";


    public static String userInputToFixMessage(String input, String id, String name) throws UserInputValidationException {
        final String[] message = input.split(" ");
        if (message.length != 5) {
            throw new UserInputValidationException("Invalid input, message should be: " + USER_MESSAGE_FORMAT);
        }
        final StringBuilder builder = new StringBuilder();
        addTag(builder, FixTag.ID, id);
        addTag(builder, FixTag.SOURCE_NAME, name);
        addTag(builder, FixTag.TARGET_NAME, message[0]);
        addTag(builder, FixTag.TYPE, message[1]);
        addTag(builder, FixTag.INSTRUMENT, message[2]);
        addTag(builder, FixTag.QUANTITY, message[3]);
        addTag(builder, FixTag.PRICE, message[4]);
        addTag(builder, FixTag.CHECKSUM, calculateChecksum(builder.toString()));
        return builder.toString();
    }

    public static String resultFixMessage(String message, String id, String srcName, String targetName, Result result) {
        final StringBuilder builder = new StringBuilder();
        addTag(builder, FixTag.ID, id);
        addTag(builder, FixTag.SOURCE_NAME, srcName);
        addTag(builder, FixTag.TARGET_NAME, targetName);
        addTag(builder, FixTag.RESULT, result.toString());
        addTag(builder, FixTag.MESSAGE, message);
        addTag(builder, FixTag.CHECKSUM, calculateChecksum(builder.toString()));
        return builder.toString();
    }

    private static void addTag(StringBuilder builder, FixTag tag, String value) {
        builder.append(tag.getValue())
                .append("=")
                .append(value)
                .append("|");
    }

    public static String getFixValueByTag(String fixMessage, FixTag tag) {
        final String[] tagValues = fixMessage.split(Pattern.quote("|"));
        final String searchPattern = tag.getValue() + "=";
        for (String tagValue : tagValues) {
            if (tagValue.startsWith(searchPattern)) {
                return tagValue.substring(searchPattern.length());
            }
        }
        throw new WrongFixTagException("No '" + tag + "' tag in message + '" + fixMessage + "'");
    }
    
    public static String calculateChecksum(String message) {
        final byte[] bytes = message.getBytes();
        int sum = 0;
        for (byte aByte : bytes) {
            sum += aByte;
        }
        return String.format("%03d", sum % 256);
    }

    public static String getMessageWithoutChecksum(String fixMessage) {
        final int checksumIndex = fixMessage.lastIndexOf(FixTag.CHECKSUM.getValue() + "=");
        return fixMessage.substring(0, checksumIndex);
    }

    
}
