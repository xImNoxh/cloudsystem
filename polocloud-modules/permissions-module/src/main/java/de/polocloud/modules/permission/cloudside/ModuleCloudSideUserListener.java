package de.polocloud.modules.permission.cloudside;

import de.polocloud.api.event.base.IListener;
import de.polocloud.api.event.handling.EventHandler;
import de.polocloud.api.event.impl.net.PacketReceiveEvent;
import de.polocloud.api.network.packets.other.DataPacket;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.base.response.ResponseState;
import de.polocloud.modules.permission.global.api.IPermissionUser;
import de.polocloud.modules.permission.global.api.PermissionPool;

import java.util.UUID;

public class ModuleCloudSideUserListener implements IListener {

    @EventHandler
    public void handle(PacketReceiveEvent event) {
        Packet packet = event.getPacket();

        if (packet instanceof DataPacket) {
            DataPacket<?> dataPacket = (DataPacket<?>) packet;
            if (dataPacket.getKey().equalsIgnoreCase("permission-module-get-user-uuid")) {
                DataPacket<UUID> uuidDataPacket = (DataPacket<UUID>) dataPacket;
                UUID data = uuidDataPacket.getData();
                IPermissionUser permissionUser = PermissionPool.getInstance().getOfflineUser(data).get();
                packet.createResponse(iNetworkResponse -> {

                    if (permissionUser == null) {
                        iNetworkResponse.setStatus(ResponseState.NULL);
                        iNetworkResponse.setSuccess(false);
                        iNetworkResponse.setError(new NullPointerException("No CloudPlayer with UUID '" + data + "' was found!"));
                    } else {
                        iNetworkResponse.setStatus(ResponseState.SUCCESS);
                        iNetworkResponse.setSuccess(true);
                        iNetworkResponse.setElement(permissionUser);
                    }
                    System.out.println("RESPONSE");
                });
            }
        }
    }
}
