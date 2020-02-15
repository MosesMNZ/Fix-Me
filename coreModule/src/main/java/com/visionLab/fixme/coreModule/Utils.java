package com.visionLab.fixme.coreModule;

import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Utils {

    public static final String INTERNAL_MESSAGE = "INTERNAL_MESSAGE:";
    public static final String EMPTY_MESSAGE = "";
    private static final String[] INSTRUMENTS = {
            "bolt", "nail", "screwdriver", "screw",
            "hammer", "saw", "drill", "wrench", "knife",
            "scissors", "toolbox", "tape", "needle"
    };

    public static String read(int bytesRead, ByteBuffer readBuffer) {
        if (bytesRead != -1) {
            readBuffer.flip();
            byte[] bytes = new byte[bytesRead];
            readBuffer.get(bytes, 0, bytesRead);
            readBuffer.clear();
            String message = new String(bytes);
            System.out.println(MessageColors.ANSI_PURPLE +
                "Received: " + message + MessageColors.ANSI_RED);
            return message;
        }
        return EMPTY_MESSAGE;
    }

    public static String readMessage(AsynchronousSocketChannel channel, ByteBuffer readBuffer) {
        try {
            return read(channel.read(readBuffer).get(), readBuffer);
        } catch (InterruptedException | ExecutionException e) {
            return EMPTY_MESSAGE;
        }
    }

    public static Future<Integer> sendInternalMessage(AsynchronousSocketChannel channel, String message) {
        System.out.println(MessageColors.ANSI_PURPLE
            + "Internal message sent: " + message + MessageColors.ANSI_RESET);
        final String internalMessage = INTERNAL_MESSAGE + message;
        return channel.write(ByteBuffer.wrap(internalMessage.getBytes()));
    }

    public static Future<Integer> sendMessage(AsynchronousSocketChannel channel, String message) {
        System.out.println(MessageColors.ANSI_PURPLE +
            "Message Sent: " + message + MessageColors.ANSI_RESET);
        return channel.write(ByteBuffer.wrap(message.getBytes()));
    }

    public static Map<String, Integer> getRandomInstruments() {
        final Map<String, Integer> instruments = new HashMap<>();
        final Random random = new Random();
        for(String instrument : INSTRUMENTS) {
            if (random.nextBoolean()) {
                instruments.put(instrument, random.nextInt(9) + 1);
            }
        }
        return instruments;
    }

    public static String getClientName(String[] args) {
        return args.length == 1
                ? args[0]
                : DateTimeFormatter.ofPattern("mmss").format(LocalDateTime.now());
    }
}
