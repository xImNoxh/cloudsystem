package de.polocloud.api.network.packets.api;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.protocol.packet.base.json.PacketSerializable;
import de.polocloud.api.network.protocol.packet.base.json.SimplePacket;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.property.def.SimpleCachedPropertyManager;
import de.polocloud.api.property.def.SimpleProperty;
import de.polocloud.api.util.AutoRegistry;
import de.polocloud.api.util.PoloHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@AutoRegistry
public class PropertyCachePacket extends Packet {

    private Map<UUID, List<IProperty>> properties;

    public PropertyCachePacket() {
        this.properties = ((SimpleCachedPropertyManager) PoloCloudAPI.getInstance().getPropertyManager()).getProperties();

    }

    public Map<UUID, List<IProperty>> getProperties() {
        Map<UUID, List<IProperty>> map = new ConcurrentHashMap<>();

        for (UUID uuid : this.properties.keySet()) {
            map.put(uuid, new ArrayList<>(properties.get(uuid)));
        }

        return map;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeInt(this.properties.size());
        for (UUID uuid : properties.keySet()) {
            List<IProperty> simpleProperties = this.properties.get(uuid);
            buf.writeUUID(uuid);
            buf.writeInt(simpleProperties.size());
            for (IProperty simpleProperty : simpleProperties) {
                buf.writeString(PoloHelper.GSON_INSTANCE.toJson(simpleProperty));
            }
        }
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        int size = buf.readInt();
        this.properties = new ConcurrentHashMap<>(size);

        for (int i = 0; i < size; i++) {
            UUID uuid = buf.readUUID();
            int properties = buf.readInt();
            List<IProperty> props = new ArrayList<>(properties);
            for (int i1 = 0; i1 < properties; i1++) {
                props.add(PoloHelper.GSON_INSTANCE.fromJson(buf.readString(), SimpleProperty.class));
            }
            this.properties.put(uuid, props);
        }
    }
}
