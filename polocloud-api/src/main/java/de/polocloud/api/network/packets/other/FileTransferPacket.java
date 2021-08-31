package de.polocloud.api.network.packets.other;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.util.AutoRegistry;

import java.io.File;
import java.io.IOException;

@AutoRegistry
public class FileTransferPacket extends Packet {

    private File file;

    public FileTransferPacket(File file) {
        this.file = file;
    }

    public FileTransferPacket() {
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeFile(file);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        this.file = buf.readFile();
    }

    public File getFile() {
        return file;
    }
}
