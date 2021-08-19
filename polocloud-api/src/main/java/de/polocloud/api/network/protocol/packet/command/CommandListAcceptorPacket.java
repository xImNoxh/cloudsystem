package de.polocloud.api.network.protocol.packet.command;

import com.google.common.collect.Lists;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.runner.ICommandRunner;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.Packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandListAcceptorPacket extends Packet {

    private List<String> commandList;
    private List<String> aliases;

    public CommandListAcceptorPacket() {
        PoloCloudAPI.getInstance().getCommandManager().getCommands().forEach(key ->{
            System.out.println(key.getCommand().name());
        });
        this.commandList = Lists.newArrayList();
        this.aliases = Lists.newArrayList();
        PoloCloudAPI.getInstance().getCommandManager().getCommands().forEach(key ->{
            this.commandList.add(key.getCommand().name());
            this.aliases.addAll(Arrays.stream(key.getCommand().aliases()).collect(Collectors.toList()));
        });
    }

    public CommandListAcceptorPacket(List<String> commandList, List<String> aliases) {
        this.commandList = commandList;
        this.aliases = aliases;
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
