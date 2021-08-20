package de.polocloud.plugin.bootstrap.spigot.register;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.protocol.packet.api.EventPacket;
import de.polocloud.api.network.protocol.packet.command.CommandListAcceptorPacket;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.protocol.register.SimplePacketRegister;

public class SpigotPacketRegister {

    public SpigotPacketRegister(CloudPlugin plugin) {


        new SimplePacketRegister<EventPacket>(EventPacket.class, eventPacket -> {

            if (!eventPacket.getExcept().equalsIgnoreCase("null") && eventPacket.getExcept().equalsIgnoreCase("cloud")) {
                return;
            }

            PoloCloudAPI.getInstance().getEventManager().fireEvent(eventPacket.getEvent());
        });
    }
}
