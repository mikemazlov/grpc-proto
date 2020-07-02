package com.example.grpc;

import io.grpc.Channel;
import io.grpc.StatusRuntimeException;

import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GrpcClient {
    private static final Logger logger = Logger.getLogger(GrpcClient.class.getName());
    private final GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub;

    public GrpcClient(Channel channel) {
        blockingStub = GrpcServiceGrpc.newBlockingStub(channel);
    }

    public String sendMessage(Message message) {
        logger.log(Level.INFO, "Try Send message");
        Request request = Request.newBuilder()
                .setRqUUID(message.getRqUID())
                .setContent(message.getContent())
                .setRqTm(message.getRqTm().format(DateTimeFormatter.ISO_DATE_TIME))
                .build();
        Response response;
        try {
            response = blockingStub.sendMessage(request);
        } catch (StatusRuntimeException exception) {
            logger.log(Level.WARNING, "Send message error {0}", exception.getStatus());
            return exception.getMessage();
        }
        logger.log(Level.INFO, "Sended message response: " + response.getStatus());
        return response.getStatus();
    }
}
