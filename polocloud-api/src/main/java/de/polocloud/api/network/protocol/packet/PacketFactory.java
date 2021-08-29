package de.polocloud.api.network.protocol.packet;

import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.util.PoloHelper;

import java.util.Map;
import java.util.TreeMap;

public class PacketFactory {

    /**
     * All registered packets with following concept
     *
     * <ID, Class of Packet>  to be able to get the class of an id
     */
    public static final Map<Integer, Class<? extends Packet>> REGISTERED_PACKETS;

    static {
        REGISTERED_PACKETS = new TreeMap<>();
    }


    /**
     * Registers a {@link Packet} automatically
     *
     * @param packet the packet
     */
    public static void registerPacket(Class<? extends Packet> packet) {
        int size = REGISTERED_PACKETS.keySet().size();
        registerPacket((size + 1), packet);
    }

    /**
     * Registers an {@link Packet} with a given id
     *
     * @param id the id of the packet
     * @param packet the class of the packet
     */
    public static void registerPacket(int id, Class<? extends Packet> packet) {
        if (REGISTERED_PACKETS.containsKey(id)) {
            registerPacket((id + 1), packet);
        }
        REGISTERED_PACKETS.put(id, packet);
    }

    /**
     * Gets the packet id based of the class
     *
     * @param clazz the class
     * @return packet id or -1
     */
    public static int getPacketId(Class<? extends Packet> clazz) {
        return REGISTERED_PACKETS.keySet().stream().filter(id -> REGISTERED_PACKETS.get(id).equals(clazz)).findAny().orElse(-1);
    }

    /**
     * Gets the class based of the packet id
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
     * @param id the packet id
     * @return packet instance
     */
    public static Packet createPacket(int id)  {
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
