package de.polocloud.api.network.protocol.packet.gameserver.permissions;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

public class PermissionCheckResponsePacket extends Packet {

    private String permission;
    private UUID player;

    private boolean response;
    private UUID request;

    public PermissionCheckResponsePacket() {
    }

    public PermissionCheckResponsePacket(UUID request, String permission, UUID player, boolean response) {
        this.permission = permission;
        this.player = player;
        this.request = request;
        this.response = response;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(request.toString());
        buf.writeString(player.toString());
        buf.writeString(permission);
        buf.writeBoolean(response);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        request = UUID.fromString(buf.readString());
        player = UUID.fromString(buf.readString());
        permission = buf.readString();
        response = buf.readBoolean();
    }

    public UUID getRequest() {
        return request;
    }

    public UUID getPlayer() {
        return player;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    public void setUuid(UUID uuid) {
        this.player = uuid;
    }
}
