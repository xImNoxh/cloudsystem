package de.polocloud.api.network.packets.wrapper;

import de.polocloud.api.config.FileConstants;
import de.polocloud.api.module.ModuleCopyType;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.util.AutoRegistry;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

@AutoRegistry
public class WrapperTransferModulesPacket extends Packet {

    private LinkedHashMap<File, ModuleCopyType[]> modulesWithInfo;

    public WrapperTransferModulesPacket(LinkedHashMap<File, ModuleCopyType[]> modulesWithInfo) {
        this.modulesWithInfo = modulesWithInfo;
    }

    public WrapperTransferModulesPacket() {
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeInt(modulesWithInfo.size());
        for (File file : modulesWithInfo.keySet()) {
            buf.writeInt(modulesWithInfo.get(file).length);
            for (ModuleCopyType moduleCopyType : modulesWithInfo.get(file)) {
                buf.writeEnum(moduleCopyType);
            }
            buf.writeFile(file);
        }
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        int moduleFilesSize = buf.readInt();
        modulesWithInfo = new LinkedHashMap<>(moduleFilesSize);

        for (int i = 0; i < moduleFilesSize; i++) {
            int arraySize = buf.readInt();
            ModuleCopyType[] enums = new ModuleCopyType[arraySize];
            for (int i1 = 0; i1 < arraySize; i1++) {
                enums[i1] = buf.readEnum();
            }

            File file = buf.readFile(FileConstants.MASTER_MODULES);
            modulesWithInfo.put(file, enums);
        }


    }

    public LinkedHashMap<File, ModuleCopyType[]> getModulesWithInfo() {
        return modulesWithInfo;
    }
}
