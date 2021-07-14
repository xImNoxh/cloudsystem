package de.polocloud.api.network.protocol.packet.api;

import de.polocloud.api.network.protocol.packet.IPacket;

import java.util.UUID;

public class APIRequestGameServerPacket implements IPacket {

    private UUID requestId;
    private Action action;
    private Object value;

    public APIRequestGameServerPacket() {

    }

    public APIRequestGameServerPacket(UUID requestId, Action action, Object value) {
        this.requestId = requestId;
        this.action = action;
        this.value = value;
    }

    public Action getAction() {
        return action;
    }

    public Object getValue() {
        return value;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public enum Action {

        NAME,
        SNOWFLAKE,
        ALL,
        LIST_BY_NAME,
        LIST_BY_TYPE,
    }

}
