package de.polocloud.api.network.protocol.packet;

import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.util.gson.PoloHelper;

import java.util.Map;
import java.util.TreeMap;

public class PacketFactory {

    /**
     * All registered packets with following concept
     * <p>
     * <ID, Class of Packet>  to be able to get the class of an id
     */
    public static final Map<Integer, Class<? extends Packet>> REGISTERED_PACKETS;

    static {
        REGISTERED_PACKETS = new TreeMap<>();
    }


    /**
     * Registers a {@link Packet} automatically
     *
     * @param packet the de.polocloud.modules.smartproxy.packet
     */
    public static void registerPacket(Class<? extends Packet> packet) {
        int size = REGISTERED_PACKETS.keySet().size();
        registerPacket((size + 1), packet);
    }

    /**
     * Registers an {@link Packet} with a given id
     *
     * @param id     the id of the de.polocloud.modules.smartproxy.packet
     * @param packet the class of the de.polocloud.modules.smartproxy.packet
     */
    public static void registerPacket(int id, Class<? extends Packet> packet) {
        if (REGISTERED_PACKETS.containsKey(id)) {
            registerPacket((id + 1), packet);
        }
        REGISTERED_PACKETS.put(id, packet);
    }

    /**
     * Gets the de.polocloud.modules.smartproxy.packet id based of the class
     *
     * @param clazz the class
     * @return de.polocloud.modules.smartproxy.packet id or -1
     */
    public static int getPacketId(Class<? extends Packet> clazz) {
        return REGISTERED_PACKETS.keySet().stream().filter(id -> REGISTERED_PACKETS.get(id).equals(clazz)).findAny().orElse(-1);
    }

    /**
     * Gets the class based of the de.polocloud.modules.smartproxy.packet id
     *
     * @param id the id
     * @return class or null
     */
    public static Class<? extends Packet> getPacketClass(int id) {
        if (!REGISTERED_PACKETS.containsKey(id)) {
            return null;
        }
        return REGISTERED_PACKETS.get(id);
    }

    /**
     * Creates a new {@link Packet} instance by a given id
     *
     * @param id the de.polocloud.modules.smartproxy.packet id
     * @return de.polocloud.modules.smartproxy.packet instance
     */
    public static Packet createPacket(int id) {
        if (!REGISTERED_PACKETS.containsKey(id)) {
            return null;
        }
        try {
            return REGISTERED_PACKETS.get(id).newInstance();
        } catch (Exception e) {
            return PoloHelper.getInstance(REGISTERED_PACKETS.get(id));
        }
    }

}
