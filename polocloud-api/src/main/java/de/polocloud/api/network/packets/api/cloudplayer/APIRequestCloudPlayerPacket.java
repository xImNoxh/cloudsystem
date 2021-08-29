package de.polocloud.api.network.packets.api.cloudplayer;

import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;
import java.util.UUID;

@AutoRegistry
public class APIRequestCloudPlayerPacket extends Packet {

    private UUID requestId;
    private Action action;
    private String value;

    public APIRequestCloudPlayerPacket() {

    }

    public APIRequestCloudPlayerPacket(UUID requestId, Action action, String value) {
        this.requestId = requestId;
        this.action = action;
        this.value = value;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeUUID(requestId);
        buf.writeString(action.toString());
        buf.writeString(this.value);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        requestId = buf.readUUID();
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

    public enum Action {
        ALL,
        BY_NAME,
        BY_UUID,
        ONLINE_UUID,
        ONLINE_NAME;
    }

}
