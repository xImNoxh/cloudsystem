package de.polocloud.api.common;

public interface IMessageable {

    /**
     * Sends this object a message
     *
     * @param message the message
     */
    void sendMessage(String message);
}
