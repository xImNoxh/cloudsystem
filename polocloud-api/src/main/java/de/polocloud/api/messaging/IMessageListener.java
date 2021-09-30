package de.polocloud.api.messaging;

import de.polocloud.api.util.other.WrappedObject;

public interface IMessageListener<T> {

    /**
     * Handles an incoming object message
     *
     * @param startTime the time it was sent
     * @param wrappedObject the received wrapped Object
     * @throws Exception if something goes wrong
     */
    void handleMessage(WrappedObject<T> wrappedObject, long startTime) throws Exception;
}
