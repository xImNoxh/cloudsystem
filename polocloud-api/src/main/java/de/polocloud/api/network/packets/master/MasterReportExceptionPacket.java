package de.polocloud.api.network.packets.master;

import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.util.PoloHelper;

import java.io.IOException;

@AutoRegistry
public class MasterReportExceptionPacket extends Packet {

    private Throwable throwable;

    public MasterReportExceptionPacket(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(throwable.getClass().getName());
        buf.writeString(PoloHelper.GSON_INSTANCE.toJson(throwable));
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        try {
            Class<?> cls = Class.forName(buf.readString());
            throwable = (Throwable) PoloHelper.GSON_INSTANCE.fromJson(buf.readString(), cls);
        } catch (Exception e) {
            throwable = null;
        }
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
