package de.polocloud.api.network.packets.api.gameserver;

import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;
import java.util.UUID;

@AutoRegistry//(id = 0x05)
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
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(requestId.toString());
        buf.writeString(action.toString());
        buf.writeString(this.value);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        requestId = UUID.fromString(buf.readString());
        action = Action.valueOf(buf.readString());
        value = buf.readString();
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

    public enum Type {
        SINGLE,
        LIST;
    }

    public enum Action {
        NAME,
        SNOWFLAKE,
        ALL,
        LIST_BY_TEMPLATE,
        LIST_BY_TYPE,
    }

}
