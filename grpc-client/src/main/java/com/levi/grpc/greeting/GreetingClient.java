package com.levi.grpc.greeting;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @ClassName: GreetingClient
 * @Description: GreetingClient
 * @Author: yyh
 * @Date: 2022/11/25 15:26
 */
public class GreetingClient {

    private static Logger logger = LoggerFactory.getLogger(GreetingClient.class);

    private GreeterGrpc.GreeterBlockingStub blockingStub;

    public static void main(String[] args) {
        // Access a service running on the local machine on port 50051
        String target = "localhost:8080";
        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build();
        GreetingClient client = new GreetingClient(channel);
        HelloReply helloReply = client.blockingStub.sayHello(HelloRequest.newBuilder().setName("L3vi").build());
        logger.info("Response: {}", helloReply.getMessage());
    }

    /** Construct client for accessing HelloWorld server using the existing channel. */
    public GreetingClient(Channel channel) {
        // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }
}
