package de.polocloud.api.network.response;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseHandler {

    private static final Map<UUID, CompletableFuture<?>> futureMap = new ConcurrentHashMap<>();

    public static <T> CompletableFuture<T> getCompletableFuture(UUID requestId, boolean autoRemove) {
        if (autoRemove) {
            return (CompletableFuture<T>) futureMap.remove(requestId);
        } else {
            return (CompletableFuture<T>) futureMap.get(requestId);
        }
    }

    public static void register(UUID requestID, CompletableFuture<?> completableFuture) {
        futureMap.put(requestID, completableFuture);
    }

}
