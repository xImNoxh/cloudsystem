package de.polocloud.plugin.bootstrap.spigot.events;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.executor.ConsoleExecutor;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.ICloudPlayerManager;
import de.polocloud.api.player.def.SimpleCloudPlayer;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.bootstrap.spigot.impl.SpigotConsoleSender;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.Plugin;

public class CollectiveSpigotEvents implements Listener {

    public CollectiveSpigotEvents(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void handle(ServerListPingEvent event) {
        IGameServer thisService = PoloCloudAPI.getInstance().getGameServerManager().getThisService();

        if (thisService != null) {
            event.setMaxPlayers(thisService.getMaxPlayers());
            event.setMotd(thisService.getMotd());
        } else {
            event.setMaxPlayers(-1);
            event.setMotd("No GameServer for '" + CloudPlugin.getCloudPluginInstance().getName() + "'");
        }
    }

    @EventHandler
    public void handle(PlayerKickEvent event) {
        if (event.getReason().contains("disconnect.spam")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handle(RemoteServerCommandEvent event) {
        CommandSender sender = event.getSender();

        ConsoleExecutor consoleCommandSender = new SpigotConsoleSender(sender);

        String command = event.getCommand().split(" ")[0];
        event.setCancelled(PoloCloudAPI.getInstance().getCommandManager().runCommand(event.getCommand(), consoleCommandSender));

    }

    @EventHandler
    public void handle(PlayerLoginEvent event) {
        if (event.getResult().equals(PlayerLoginEvent.Result.KICK_FULL)) event.allow();

        Player player = event.getPlayer();

        if (PoloCloudAPI.getInstance().getGameServerManager().getThisService().getTemplate().isMaintenance() && !player.hasPermission("*") && !player.hasPermission("cloud.maintenance")) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, PoloCloudAPI.getInstance().getMasterConfig().getMessages().getGroupMaintenanceMessage());
            return;
        }

        if (Bukkit.getOnlinePlayers().size() >= PoloCloudAPI.getInstance().getGameServerManager().getThisService().getMaxPlayers() && !player.hasPermission("*") && !player.hasPermission("cloud.fulljoin")) {
            event.disallow(PlayerLoginEvent.Result.KICK_FULL, PoloCloudAPI.getInstance().getMasterConfig().getMessages().getServiceIsFull());
            return;
        }

        Scheduler.runtimeScheduler().schedule(() -> {
            IGameServer thisService = PoloCloudAPI.getInstance().getGameServerManager().getThisService();
            ICloudPlayerManager playerManager = PoloCloudAPI.getInstance().getCloudPlayerManager();
            ICloudPlayer cached = playerManager.getCached(player.getName());
            if (cached != null) {
                ((SimpleCloudPlayer)cached).setMinecraftServer(thisService.getName());
                cached.update();
            }
        }, () -> PoloCloudAPI.getInstance().getGameServerManager().getThisService() != null);
    }

    @EventHandler
    public void onCmd(PlayerCommandPreprocessEvent event) {

        try {
            event.setCancelled(PoloCloudAPI.getInstance().getCommandManager().runCommand(event.getMessage(), PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(event.getPlayer().getName())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
