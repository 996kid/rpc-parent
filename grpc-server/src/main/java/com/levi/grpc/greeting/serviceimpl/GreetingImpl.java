package com.levi.grpc.greeting.serviceimpl;

import com.levi.grpc.greeting.GreeterGrpc;
import com.levi.grpc.greeting.HelloReply;
import com.levi.grpc.greeting.HelloRequest;
import io.grpc.stub.StreamObserver;

/**
 * @ClassName: GreetingImpl
 * @Description: TODO
 * @Author: yyh
 * @Date: 2022/11/25 15:04
 */
public class GreetingImpl extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void sayHelloAgain(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello again " + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
