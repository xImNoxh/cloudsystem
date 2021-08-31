package de.polocloud.api.messaging;

public interface IMessageListener<T> {

    /**
     * Handles an incoming object message
     *
     * @param startTime the time it was sent
     * @param t the received object
     * @throws Exception if something goes wrong
     */
    void handleMessage(T t, long startTime) throws Exception;
}
