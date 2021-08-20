package de.polocloud.api.event.base;

import de.polocloud.api.network.protocol.buffer.IProtocolObject;

public interface IEvent extends IProtocolObject {

    /**
     * If packet should be globally fired
     */
    default boolean globalFire() {
        return true;
    }

}
