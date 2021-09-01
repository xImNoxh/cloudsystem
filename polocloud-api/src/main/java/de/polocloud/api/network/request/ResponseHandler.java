package de.polocloud.api.network.request;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class ResponseHandler {

    private static final Map<UUID, Future<?>> futureMap = new ConcurrentHashMap<>();

    public static <T> CompletableFuture<T> getCompletableFuture(UUID requestId, boolean autoRemove) {
        if (autoRemove) {
            return (CompletableFuture<T>) futureMap.remove(requestId);
        } else {
            return (CompletableFuture<T>) futureMap.get(requestId);
        }
    }

    public static void register(UUID requestID, Future<?> completableFuture) {
        futureMap.put(requestID, completableFuture);
    }

}
