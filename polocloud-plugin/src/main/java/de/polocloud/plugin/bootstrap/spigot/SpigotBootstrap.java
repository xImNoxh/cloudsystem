package de.polocloud.plugin.bootstrap.spigot;

import de.polocloud.api.bridge.PoloPluginBridge;
import de.polocloud.api.common.PoloType;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.bootstrap.IBootstrap;
import de.polocloud.plugin.bootstrap.spigot.events.CollectiveSpigotEvents;
import de.polocloud.plugin.bootstrap.spigot.register.SpigotPacketRegister;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class SpigotBootstrap extends JavaPlugin implements IBootstrap {

    private CloudPlugin cloudPlugin;
    private PoloPluginBridge bridge;

    @Override
    public synchronized void onLoad() {
        this.bridge = new PoloPluginBridge() {
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

                player.kickPlayer(reason);
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
                Bukkit.getScheduler().runTask(SpigotBootstrap.this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
            }

            @Override
            public PoloType getEnvironment() {
                return PoloType.PLUGIN_SPIGOT;
            }

            @Override
            public void sendActionbar(UUID uniqueId, String message) {
                //TODO
            }

            @Override
            public void sendTabList(UUID uniqueId, String header, String footer) {
                //TODO
            }

            @Override
            public void shutdown() {
                Bukkit.shutdown();
            }
        };
        this.cloudPlugin = new CloudPlugin(this);
    }

    @Override
    public synchronized void onEnable() {
        this.cloudPlugin.onEnable();
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
        new SpigotPacketRegister(CloudPlugin.getCloudPluginInstance());
    }
}
