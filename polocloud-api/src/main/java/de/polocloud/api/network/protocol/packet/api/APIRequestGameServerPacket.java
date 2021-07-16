package de.polocloud.api.network.protocol.packet.api;

import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

public class APIRequestGameServerPacket extends Packet {


    private UUID requestId;
    private Action action;
    private String value;

    public APIRequestGameServerPacket() {

    }

    public APIRequestGameServerPacket(UUID requestId, Action action, String value) {
        this.requestId = requestId;
        this.action = action;
        this.value = value;
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, requestId.toString());
        writeString(byteBuf, action.toString());
        writeString(byteBuf, this.value);
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        requestId = UUID.fromString(readString(byteBuf));
        action = Action.valueOf(readString(byteBuf));
        value = readString(byteBuf);
    }


    public Action getAction() {
        return action;
    }

    public String getValue() {
        return value;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public enum Action {

        NAME,
        SNOWFLAKE,
        ALL,
        LIST_BY_TEMPLATE,
        LIST_BY_TYPE,
    }

}
