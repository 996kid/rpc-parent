package com.levi.grpc.greeting;

import com.levi.grpc.greeting.serviceimpl.GreetingImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: GreetingServer
 * @Description: Greeting grpc server
 * @Author: yyh
 * @Date: 2022/11/25 14:55
 */
public class GreetingServer {

    private Logger logger = LoggerFactory.getLogger(GreetingServer.class);

    private Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        GreetingServer greetingServer = new GreetingServer();
        greetingServer.start();
        greetingServer.blockUntilShutdown();
    }

    private void start() throws IOException {
        int port = 8080;
        server = ServerBuilder.forPort(port)
                .addService(new GreetingImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                GreetingServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
        }));
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
