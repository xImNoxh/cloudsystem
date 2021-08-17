package de.polocloud.api.network.protocol.packet.command;

import com.google.common.collect.Lists;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandListAcceptorPacket extends Packet {

    private List<String> commandList;
    private List<String> aliases;

    public CommandListAcceptorPacket() {
        this.commandList = Lists.newArrayList();
        this.aliases = Lists.newArrayList();
    }

    public CommandListAcceptorPacket(List<String> commandList, List<String> aliases) {
        this.commandList = commandList;
        this.aliases = aliases;

        PoloCloudAPI.getInstance().getCommandPool().getAllCachedCommands().stream().filter(key -> key.getCommandType().equals(CommandType.INGAME_CONSOLE)
            || key.getCommandType().equals(CommandType.INGAME)).collect(Collectors.toList()).forEach(key -> {
            this.commandList.add(key.getName());
            this.aliases.addAll(Arrays.stream(key.getAliases()).collect(Collectors.toList()));
        });
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(String.join(";", commandList));
        buf.writeString(String.join(";", aliases));
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        commandList = Arrays.stream(buf.readString().split(";")).collect(Collectors.toList());
        aliases = Arrays.stream(buf.readString().split(";")).collect(Collectors.toList());
    }

    public List<String> getCommandList() {
        return commandList;
    }

    public List<String> getAliases() {
        return aliases;
    }
}
