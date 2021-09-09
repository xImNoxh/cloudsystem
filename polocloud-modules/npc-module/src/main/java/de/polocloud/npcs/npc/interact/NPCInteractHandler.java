package de.polocloud.npcs.npc.interact;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.npcs.npc.base.ICloudNPC;
import de.polocloud.npcs.plugin.inventory.ServerSelectorInventory;
import org.bukkit.entity.Player;

public class NPCInteractHandler {

    public static void handleConnect(Player player, ICloudNPC npc){
        IGameServer gameServer = PoloCloudAPI.getInstance().getGameServerManager().getCached(npc.getCloudNPCMetaData().getTemplateOrGameServer());
        if (gameServer == null) {
            player.sendMessage(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix() + "§7The gameserver §8» §b" + npc.getCloudNPCMetaData().getTemplateOrGameServer() + " §7is currently §cunavailable§7!");
        }else{
            if(gameServer.getTemplate().isMaintenance()){
                player.sendMessage(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix() + "§7The gameserver §8» §b" + npc.getCloudNPCMetaData().getTemplateOrGameServer() + " §7is currently in §cmaintenance§7!");
            }else{
                ICloudPlayer cloudPlayer = PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(player.getName());
                if (cloudPlayer == null) {
                    player.sendMessage("§cAn internal PoloCloud-PlayerManager error occurred!");
                    return;
                }
                if(cloudPlayer.getMinecraftServer() != null && (cloudPlayer.getMinecraftServer().getSnowflake() == gameServer.getSnowflake())){
                    player.sendMessage(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix() + "§7You are already on the gameserver §8» §b" + npc.getCloudNPCMetaData().getTemplateOrGameServer() + "§7!");
                }else{
                    if(gameServer.getOnlinePlayers() >= gameServer.getTemplate().getMaxPlayers()){
                        player.sendMessage(PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix() + "§7The gameserver §8» §b" + npc.getCloudNPCMetaData().getTemplateOrGameServer() + " §7is currently §cfull§7!");
                        return;
                    }
                    cloudPlayer.sendTo(gameServer);
                }
            }
        }
    }

    public static void handleOpenInventory(Player player, ICloudNPC npc){
        new ServerSelectorInventory(PoloCloudAPI.getInstance().getTemplateManager().getTemplate(npc.getCloudNPCMetaData().getTemplateOrGameServer()), player).open();
    }

}
