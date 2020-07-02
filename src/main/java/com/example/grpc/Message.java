package com.example.grpc;

import java.time.ZonedDateTime;
import java.util.UUID;

public class Message {
    private final String rqUID;
    private final ZonedDateTime rqTm;
    private final String content;

    public Message(String content) {
        this.rqUID = UUID.randomUUID().toString();
        this.rqTm = ZonedDateTime.now();
        this.content = content;

    }

    public String getRqUID() {
        return rqUID;
    }

    public ZonedDateTime getRqTm() {
        return rqTm;
    }

    public String getContent() {
        return content;
    }
}
