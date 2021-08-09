package de.polocloud.plugin.bootstrap.spigot.register;

import de.polocloud.api.network.protocol.packet.command.CommandListAcceptorPacket;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.protocol.register.SimplePacketRegister;

public class SpigotPacketRegister {

    public SpigotPacketRegister(CloudPlugin plugin) {
        new SimplePacketRegister<CommandListAcceptorPacket>(CommandListAcceptorPacket.class, packet -> {
            plugin.getCommandReader().setAllowedCommands(packet.getCommandList());
            plugin.getCommandReader().getAllowedCommands().addAll(packet.getAliases());
        });
    }
}
