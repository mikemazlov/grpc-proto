package com.example.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GrpcServer {
    private Server server;
    private final RequestHandler requestHandler;
    private static final Logger logger = Logger.getLogger(GrpcServer.class.getName());

    public GrpcServer(RequestHandler requestHandler){
        if (requestHandler == null) throw new IllegalArgumentException("Request handler is null");
        this.requestHandler = requestHandler;
    }

    private static class GrpcServiceImpl extends GrpcServiceGrpc.GrpcServiceImplBase {
        private RequestHandler requestHandler;
        @Override
        public void sendMessage(Request request, StreamObserver<Response> responseObserver) {
            logger.log(Level.INFO, "Grpc server get request:\n" + request.toString());
            responseObserver.onNext(requestHandler.handleRequest(request));
            responseObserver.onCompleted();
        }
    }

    public void start() throws IOException {
        int port = 50052;
        GrpcServiceImpl grpcService = new GrpcServiceImpl();
        grpcService.requestHandler = requestHandler;
        server = ServerBuilder.forPort(port)
                .addService(grpcService)
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.log(Level.INFO, "*** shutting down gRPC server since JVM is shutting down");
            try {
                GrpcServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            logger.log(Level.INFO, "*** server shut down");
        }));
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
