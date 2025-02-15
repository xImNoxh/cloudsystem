package de.polocloud.api.gameserver.helper;

import com.google.common.cache.AbstractLoadingCache;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public enum MinecraftProtocol {

    UNKNOWN(-1, "Unknown"),
    LEGACY(-2, "Legacy"),
    MINECRAFT_1_7_2(4, "1.7.2", "1.7.3", "1.7.4", "1.7.5"),
    MINECRAFT_1_7_6(5, "1.7.6", "1.7.7", "1.7.8", "1.7.9", "1.7.10"),
    MINECRAFT_1_8(47, "1.8", "1.8.1", "1.8.2", "1.8.3", "1.8.4", "1.8.5", "1.8.6", "1.8.7", "1.8.8", "1.8.9"),
    MINECRAFT_1_9(107, "1.9"),
    MINECRAFT_1_9_1(108, "1.9.1"),
    MINECRAFT_1_9_2(109, "1.9.2"),
    MINECRAFT_1_9_4(110, "1.9.3", "1.9.4"),
    MINECRAFT_1_10(210, "1.10", "1.10.1", "1.10.2"),
    MINECRAFT_1_11(315, "1.11"),
    MINECRAFT_1_11_1(316, "1.11.1", "1.11.2"),
    MINECRAFT_1_12(335, "1.12"),
    MINECRAFT_1_12_1(338, "1.12.1"),
    MINECRAFT_1_12_2(340, "1.12.2"),
    MINECRAFT_1_13(393, "1.13"),
    MINECRAFT_1_13_1(401, "1.13.1"),
    MINECRAFT_1_13_2(404, "1.13.2"),
    MINECRAFT_1_14(477, "1.14"),
    MINECRAFT_1_14_1(480, "1.14.1"),
    MINECRAFT_1_14_2(485, "1.14.2"),
    MINECRAFT_1_14_3(490, "1.14.3"),
    MINECRAFT_1_14_4(498, "1.14.4"),
    MINECRAFT_1_15(573, "1.15"),
    MINECRAFT_1_15_1(575, "1.15.1"),
    MINECRAFT_1_15_2(578, "1.15.2"),
    MINECRAFT_1_16(735, "1.16"),
    MINECRAFT_1_16_1(736, "1.16.1"),
    MINECRAFT_1_16_2(751, "1.16.2"),
    MINECRAFT_1_16_3(753, "1.16.3"),
    MINECRAFT_1_16_4(754, "1.16.4", "1.16.5"),
    MINECRAFT_1_17(755, "1.17");

    /**
     * The protocols cached with a given ID
     */
    public static final Map<Integer, MinecraftProtocol> ID_TO_PROTOCOL_CONSTANT = Maps.toMap(() -> {
        List<Integer> integers = new LinkedList<>();
        for (MinecraftProtocol value : values()) {
            integers.add(value.getProtocolId());
        }
        return integers.iterator();
    }, new AbstractLoadingCache<Integer, MinecraftProtocol>() {
        @Override
        public MinecraftProtocol get(@NotNull Integer integer) {
            return Arrays.stream(values()).filter(minecraftProtocol -> minecraftProtocol.getProtocolId() == integer).findFirst().orElse(null);
        }

        @Override
        public MinecraftProtocol getIfPresent(@NotNull Object o) {
            return Arrays.stream(values()).filter(minecraftProtocol -> minecraftProtocol.equals(o)).findFirst().orElse(null);
        }
    });

    /**
     * The highest version at the moment
     */
    public static final MinecraftProtocol HIGHEST_VERSION = values()[values().length - 1];

    /**
     * The protocol id
     */
    private final int protocolId;

    /**
     * All allowed names
     */
    private final String[] versionNames;

    MinecraftProtocol(int protocolId, String... versionNames) {
        this.protocolId = protocolId;
        this.versionNames = versionNames;
    }

    public int getProtocolId() {
        return protocolId;
    }

    /**
     * The name of this protocol
     * 
     * @return name as {@link String}
     */
    public String getName() {
        return this.versionNames[0];
    }

    public String[] getVersionNames() {
        return versionNames;
    }

    /**
     * Gets the highest version of this version
     * 
     * @return name of version
     */
    public String getNewestVersion() {
        return this.versionNames[this.versionNames.length - 1];
    }

    /**
     * Searches for a {@link MinecraftProtocol} with a given id
     * If not found it will return {@link MinecraftProtocol#UNKNOWN}
     * 
     * @param id the protocol id
     * @return version
     */
    public static MinecraftProtocol valueOf(int id) {
        return ID_TO_PROTOCOL_CONSTANT.getOrDefault(id, UNKNOWN);
    }

}
