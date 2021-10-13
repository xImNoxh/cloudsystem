package de.polocloud.plugin.bootstrap.spigot;

import de.polocloud.api.bridge.PoloPluginBridge;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.util.PoloHelper;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.bootstrap.IBootstrap;
import de.polocloud.plugin.bootstrap.spigot.events.CollectiveSpigotEvents;
import de.polocloud.plugin.bootstrap.spigot.register.SpigotPacketRegister;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class SpigotBootstrap extends JavaPlugin implements IBootstrap {

    private PoloPluginBridge bridge;


    @Override
    public synchronized void onEnable() {
        this.bridge = new PoloPluginBridge() {

            @Override
            public long getPing(UUID uniqueId) {
                Player player = Bukkit.getPlayer(uniqueId);
                if (player == null) {
                    return -1;
                }
                try {
                    Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
                    return (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
                return -1;
            }

            @Override
            public boolean isPlayerOnline(UUID uniqueId) {
                return Bukkit.getPlayer(uniqueId) != null;
            }

            @Override
            public boolean hasPermission(UUID uniqueId, String permission) {
                Player player = Bukkit.getPlayer(uniqueId);

                if (player == null) {
                    return false;
                }
                return player.hasPermission(permission);
            }

            @Override
            public void sendMessage(UUID uniqueId, String message) {
                Player player = Bukkit.getPlayer(uniqueId);

                if (player == null) {
                    return;
                }

                player.sendMessage(message);
            }

            @Override
            public void broadcast(String message) {
                Bukkit.broadcastMessage(message);
            }

            @Override
            public void kickPlayer(UUID uniqueId, String reason) {

                Player player = Bukkit.getPlayer(uniqueId);

                if (player == null) {
                    return;
                }

                Bukkit.getScheduler().runTask(SpigotBootstrap.this, () -> player.kickPlayer(reason));
            }

            @Override
            public void sendTitle(UUID uniqueId, String title, String subTitle) {

                Player player = Bukkit.getPlayer(uniqueId);

                if (player == null) {
                    return;
                }
                player.sendTitle(title, subTitle);
            }

            @Override
            public void executeCommand(String command) {
                if (!SpigotBootstrap.this.isEnabled()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    return;
                }
                Bukkit.getScheduler().runTask(SpigotBootstrap.this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
            }

            @Override
            public PoloType getEnvironment() {
                return PoloType.PLUGIN_SPIGOT;
            }

            @Override
            public void sendActionbar(UUID uniqueId, String message) {
                try {
                    Player player = Bukkit.getPlayer(uniqueId);

                    if (player == null || message == null) {
                        return;
                    }

                    String nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
                    nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);

                    if (!nmsVersion.startsWith("v1_9_R") && !nmsVersion.startsWith("v1_8_R")) {

                        Method getSpigot = player.getClass().getDeclaredMethod("spigot"); getSpigot.setAccessible(true);
                        Object spigot = getSpigot.invoke(player);
                        Method sendMessage = spigot.getClass().getDeclaredMethod("sendMessage", ChatMessageType.class, TextComponent.class); sendMessage.setAccessible(true);

                        sendMessage.invoke(spigot, ChatMessageType.ACTION_BAR, new TextComponent(message));
                        return;
                    }

                    Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
                    Object craftPlayer = craftPlayerClass.cast(player);

                    Class<?> ppoc = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
                    Class<?> p = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
                    Object packetPlayOutChat;
                    Class<?> chat = Class.forName("net.minecraft.server." + nmsVersion + (nmsVersion.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
                    Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");

                    Method method = null;
                    if (nmsVersion.equalsIgnoreCase("v1_8_R1")) method = chat.getDeclaredMethod("a", String.class);

                    Object object = nmsVersion.equalsIgnoreCase("v1_8_R1") ? chatBaseComponent.cast(method.invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(new Class[]{String.class}).newInstance(message);
                    packetPlayOutChat = ppoc.getConstructor(new Class[]{chatBaseComponent, Byte.TYPE}).newInstance(object, (byte) 2);

                    Method handle = craftPlayerClass.getDeclaredMethod("getHandle");
                    Object iCraftPlayer = handle.invoke(craftPlayer);
                    Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
                    Object playerConnection = playerConnectionField.get(iCraftPlayer);
                    Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", p);
                    sendPacket.invoke(playerConnection, packetPlayOutChat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void sendTabList(UUID uniqueId, String header, String footer) {
                try {
                    Player player = Bukkit.getPlayer(uniqueId);
                    String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

                    if (player == null) {
                        return;
                    }

                    Class<?> chatMessageClass = PoloHelper.getNMSClass("ChatMessage", version);

                    Object tablistHeader = chatMessageClass.getConstructor(String.class, Object[].class).newInstance(header, new Object[0]);
                    Object tablistFooter = chatMessageClass.getConstructor(String.class, Object[].class).newInstance(footer, new Object[0]);

                    Object tablist = PoloHelper.getNMSClass("PacketPlayOutPlayerListHeaderFooter", version).newInstance();
                    try {
                        Field headerField = tablist.getClass().getDeclaredField("a");
                        headerField.setAccessible(true);
                        headerField.set(tablist, tablistHeader);
                        headerField.setAccessible(!headerField.isAccessible());
                        Field footerField = tablist.getClass().getDeclaredField("b");
                        footerField.setAccessible(true);
                        footerField.set(tablist, tablistFooter);
                        footerField.setAccessible(!footerField.isAccessible());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    PoloHelper.sendPacket(player, version, tablist);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void shutdown() {
                Bukkit.shutdown();
            }
        };

        CloudPlugin cloudPlugin = new CloudPlugin(this);
        cloudPlugin.connectToCloud();
    }

    @Override
    public synchronized void onDisable() {

    }

    @Override
    public int getPort() {
        return Bukkit.getPort();
    }

    @Override
    public void registerListeners() {
        new CollectiveSpigotEvents(this);
    }

    @Override
    public PoloPluginBridge getBridge() {
        return bridge;
    }

    @Override
    public void registerPacketListening() {
        new SpigotPacketRegister(CloudPlugin.getInstance());
    }
}
