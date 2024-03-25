package com.levi.grpc.etcd.demo;

import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import io.grpc.stub.StreamObserver;
import io.vertx.core.json.Json;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName: Usage
 * @Description: Usage
 * @Author: yyh
 * @Date: 2022/11/25 17:48
 */
public class Usage {

    /**
     * 本地服务集合缓存
     * {"addr": "", "port": 8888, "metadata": ""}
     */
    static Map<String, List<Endpoint>> serviceCollection = new ConcurrentHashMap<>(32);

    public static void main(String[] args) throws InterruptedException, ExecutionException, UnsupportedEncodingException {
//        registryServices();
        discoverServices("foo/bar/my-service/1.2.3.4");
    }


    // 注册服务 发送心跳包间隔 为 ttl的1/3
    private static void registryServices() throws ExecutionException, InterruptedException, UnsupportedEncodingException {
        // create client using endpoints
        Client client = Client.builder().target("http://localhost:2379").build();
        Lease lease = client.getLeaseClient();
        // etcdctl lease grant 5
        CompletableFuture<LeaseGrantResponse> responseFuture = lease.grant(60);
        LeaseGrantResponse response = responseFuture.get();
        long leaseId = response.getID();
        System.out.println(String.format("", leaseId));
        PutOption putOption = PutOption.newBuilder().withLeaseId(leaseId).build();
        String serviceName = "foo/bar/my-service/1.2.3.4";
        String metadata = "{\"addr\":\"1.2.3.4\",\"port\":\"8888\",\"metadata\":\"...\"}";
        // etcdctl put --lease=$lease my-service/1.2.3.4 '{"Addr":"1.2.3.4","Metadata":"..."}'
        client.getKVClient().put(ByteSequence.from(serviceName.getBytes("utf-8")),
                ByteSequence.from(metadata.getBytes("utf-8")), putOption);

        // etcdctl lease keep-alive $lease
        lease.keepAlive(leaseId, new StreamObserver() {
            @Override
            public void onNext(Object o) {
                System.out.println("StreamObserver.onNext: " + o);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("StreamObserver.onError: " + throwable);
            }

            @Override
            public void onCompleted() {
                System.out.println("StreamObserver.onCompleted");
            }
        });

        Thread.sleep(Integer.MAX_VALUE);
        client.close();
    }

    private static void discoverServices(String serviceName) throws UnsupportedEncodingException, ExecutionException, InterruptedException {
        Client client = Client.builder().target("http://localhost:2379").build();
        KV kv = client.getKVClient();
        Watch watch = client.getWatchClient();
        CompletableFuture<GetResponse> completableFuture = kv.get(ByteSequence.from(serviceName.getBytes("utf-8")));
        GetResponse getResponse = completableFuture.get();
        List<KeyValue> kvs = getResponse.getKvs();
        /**
         * 保存服务信息到本地缓存
         * 监控服务状态
         */
        saveToLocalStorage(kvs, serviceName);
        watch(watch, serviceName);
    }

    private static void watch(Watch watch, String serviceName) throws UnsupportedEncodingException {
        watch.watch(ByteSequence.from(serviceName.getBytes("utf-8")), new Watch.Listener() {
            @Override
            public void onNext(WatchResponse response) {
                // 更新本地服务列表
                List<WatchEvent> events = response.getEvents();
                for (WatchEvent event : events) {
                    System.out.println(event.getEventType());
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    private static void saveToLocalStorage(List<KeyValue> kvs, String serviceName) {
        if (!CollectionUtils.isEmpty(kvs)) {
            KeyValue kv = kvs.get(0);
            String serviceInfo = kv.getValue().toString();
            List<Endpoint> endpoints = serviceCollection.get(kv.getKey());
            Endpoint endpoint = Json.decodeValue(serviceInfo, Endpoint.class);
            if (CollectionUtils.isEmpty(endpoints)) {
                List<Endpoint> newEndpoints = new ArrayList<>();
                newEndpoints.add(endpoint);
                serviceCollection.put(kv.getKey().toString(), newEndpoints);
            } else {
                endpoints.add(endpoint);
                serviceCollection.put(kv.getKey().toString(), endpoints);
            }
        }
    }
    
    
}
