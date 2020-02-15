package com.visionLab.fixme.coreModule;

public enum MessageType {
    Buy,
    Sell;

    public static boolean is(String type) {
        return type.equals(Buy.toString()) || type.equals(Sell.toString());
    }
}
