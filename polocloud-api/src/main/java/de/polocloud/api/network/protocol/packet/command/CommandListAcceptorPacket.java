package de.polocloud.api.network.protocol.packet.command;

import com.google.common.collect.Lists;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.commands.CommandType;
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
        commandList = Lists.newArrayList();
        aliases = Lists.newArrayList();

        CloudAPI.getInstance().getCommandPool().getAllCachedCommands().stream().filter(key -> key.getCommandType().equals(CommandType.INGAME_CONSOLE)
            || key.getCommandType().equals(CommandType.INGAME)).collect(Collectors.toList()).forEach(key -> {
                commandList.add(key.getName());
                aliases.addAll(Arrays.stream(key.getAliases()).collect(Collectors.toList()));
        });
    }

    @Override
    public void write(ByteBuf byteBuf) throws IOException {
        writeString(byteBuf, String.join(";", commandList));
        writeString(byteBuf, String.join(";", aliases));
    }

    @Override
    public void read(ByteBuf byteBuf) throws IOException {
        commandList = Arrays.stream(readString(byteBuf).split(";")).collect(Collectors.toList());
        aliases = Arrays.stream(readString(byteBuf).split(";")).collect(Collectors.toList());
    }

    public List<String> getCommandList() {
        return commandList;
    }

    public List<String> getAliases() {
        return aliases;
    }
}
