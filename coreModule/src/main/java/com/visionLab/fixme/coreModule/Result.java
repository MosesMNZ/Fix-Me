package com.visionLab.fixme.coreModule;

public enum Result {
    Executed,
    Rejected;

    public static boolean is(String result) {
        return result.equals(Executed.toString()) || result.equals(Rejected.toString());
    }
}
