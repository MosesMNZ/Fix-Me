package com.visionLab.fixme.coreModule.handler;

import java.nio.channels.*;

public interface MessageHandler {

    void setNext(MessageHandler handler);

    void handle(AsynchronousSocketChannel clientChannel, String message);
}
