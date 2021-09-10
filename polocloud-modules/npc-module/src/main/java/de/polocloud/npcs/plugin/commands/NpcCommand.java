package de.polocloud.npcs.plugin.commands;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.npcs.bootstraps.PluginBootstrap;
import de.polocloud.npcs.npc.base.ICloudNPC;
import de.polocloud.npcs.npc.base.impl.SimpleCloudNPC;
import de.polocloud.npcs.npc.base.impl.SimpleEntityNPC;
import de.polocloud.npcs.npc.base.meta.CloudNPCMeta;
import de.polocloud.npcs.npc.entity.EntityProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NpcCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("§cThis command is only usable ingame§8.");
            return false;
        }
        Player player = (Player)sender;
        String prefix = PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix() + "[NPCs] ";


        if(!player.hasPermission("cloud.npcs.command.use")){
            sender.sendMessage("§cYou have no permission for this command!");
            return false;
        }

        if(args.length == 1) {
            switch (args[0]) {
                case "list":
                    if (PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getCloudNPCS().size() == 0) {
                        player.sendMessage(prefix + "§7Currently there are §cno §bNPCs§7!");
                        return false;
                    }
                    player.sendMessage(prefix + "§7Listing...");
                    player.sendMessage(prefix + "§7----[§bNPCs§7(§b" + PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getCloudNPCS().size() + "§7)]----");
                    for (ICloudNPC cloudNPC : PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getCloudNPCS()) {
                        player.sendMessage(prefix + "§8» §b" + cloudNPC.getCloudNPCMetaData().getTemplateOrGameServer());
                        player.sendMessage("§7 -> [§b" + cloudNPC.getCloudNPCMetaData().getTemplateOrGameServer() + "§7] §7isEntity? §8» §b" + cloudNPC.getCloudNPCMetaData().isEntity());
                        player.sendMessage("§7 -> [§b" + cloudNPC.getCloudNPCMetaData().getTemplateOrGameServer() + "§7] §7isOnlyForGameServer? §8» §b" + cloudNPC.getCloudNPCMetaData().isOnlyGameServer());
                        player.sendMessage("§7 -> [§b" + cloudNPC.getCloudNPCMetaData().getTemplateOrGameServer() + "§7] " + (cloudNPC.getCloudNPCMetaData().isEntity() ? "§7EntityType §8» §b" + cloudNPC.getCloudNPCMetaData().getEntityName() : "§7Skin §8» §b" + (cloudNPC.getCloudNPCMetaData().getSkinName().equals("default") ? "default (HttpMarco)" : cloudNPC.getCloudNPCMetaData().getSkinName())));
                        player.sendMessage("§7 -> [§b" + cloudNPC.getCloudNPCMetaData().getTemplateOrGameServer() + "§7] §7location §8» §b" + cloudNPC.getLocation().getX() + "§7, §b" + cloudNPC.getCloudNPCMetaData().getY() + "§7, §b" + cloudNPC.getCloudNPCMetaData().getZ());
                    }
                    break;
                case "update":
                    PluginBootstrap.getInstance().getNpcService().updateNPCs();
                    break;
                case "remove":
                    Location roundLocation = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
                    ICloudNPC npc = PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getNPCByLocation(roundLocation);
                    if (npc == null) {
                        player.sendMessage(prefix + "§7There is no §bNPC §7on your location!");
                        return false;
                    }
                    npc.remove();
                    PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getCloudNPCS().remove(npc);
                    PluginBootstrap.getInstance().getNpcService().updateNPCs();
                    player.sendMessage(prefix + "§7You §cremoved §7the §bNPC §7on your location!");
                    break;
                default:
                    sendHelp(player);
                    break;
            }
        }else if(args.length == 5){
            if(args[0].equals("create")){
                Location roundLocation = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
                if (PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getNPCByLocation(roundLocation) != null) {
                    player.sendMessage(prefix + "§7There is already a §bNPC §7on your location!");
                    return false;
                }
                if(args[1].equalsIgnoreCase("template")){
                    String templateName = args[2];
                    ITemplate template = PoloCloudAPI.getInstance().getTemplateManager().getTemplate(templateName);
                    if (template == null) {
                        player.sendMessage(prefix + "§7The template §8» §b" + templateName + " §cdoesn't exist§7!");
                        return false;
                    }
                    if(args[3].equalsIgnoreCase("entity")){
                        String entityName = args[4];
                        if(!EntityProvider.getAvailableStringTypes().contains(entityName.toLowerCase())){
                            player.sendMessage(prefix + "§7The entitytype §8» §b" + entityName + " §cdoesn't exist§7!");
                            return false;
                        }
                        ICloudNPC npc = new SimpleEntityNPC(new CloudNPCMeta(roundLocation.getBlockX(), roundLocation.getBlockY(), roundLocation.getBlockZ(), roundLocation.getWorld().getName(), template.getName(), "none", "none", entityName.toUpperCase(), false, true, player.getLocation().getYaw(), player.getLocation().getPitch()));
                        npc.spawn();
                        PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getCloudNPCS().add(npc);
                        PluginBootstrap.getInstance().getNpcService().updateNPCs();
                    }else{
                        sendHelp(player);
                    }
                }else if(args[1].equalsIgnoreCase("gameserver")){
                    String gameServerName = args[2];
                    IGameServer gameServer = PoloCloudAPI.getInstance().getGameServerManager().getCached(gameServerName);
                    if (gameServer == null) {
                        player.sendMessage(prefix + "§7The gameserver §8» §b" + gameServerName + " §cisn't online§7!");
                        return false;
                    }
                    if(args[3].equalsIgnoreCase("entity")){
                        String entityName = args[4];
                        if(!EntityProvider.getAvailableStringTypes().contains(entityName.toLowerCase())){
                            player.sendMessage(prefix + "§7The entitytype §8» §b" + entityName + " §cdoesn't exist§7!");
                            return false;
                        }
                        ICloudNPC npc = new SimpleEntityNPC(new CloudNPCMeta(roundLocation.getBlockX(), roundLocation.getBlockY(), roundLocation.getBlockZ(), roundLocation.getWorld().getName(), gameServer.getName(), "none", "none", entityName.toUpperCase(), true, true, player.getLocation().getYaw(), player.getLocation().getPitch()));
                        npc.spawn();
                        PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getCloudNPCS().add(npc);
                        PluginBootstrap.getInstance().getNpcService().updateNPCs();
                    }else{
                        sendHelp(player);
                    }
                }else{
                    sendHelp(player);
                }
            }else{
                sendHelp(player);
            }
        }else if(args.length == 6){
            if(args[0].equals("create")){
                Location roundLocation = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
                if (PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getNPCByLocation(roundLocation) != null) {
                    player.sendMessage(prefix + "§7There is already a §bNPC §7on your location!");
                    return false;
                }

                if(args[1].equalsIgnoreCase("template")){
                    String templateName = args[2];
                    ITemplate template = PoloCloudAPI.getInstance().getTemplateManager().getTemplate(templateName);
                    if (template == null) {
                        player.sendMessage(prefix + "§7The template §8» §b" + templateName + " §cdoesn't exist§7!");
                        return false;
                    }
                    if(args[3].equalsIgnoreCase("npc")){
                        String skinName = args[4];
                        String itemName = args[5];
                        Material itemMaterial = Material.getMaterial(itemName);
                        if(!itemName.equalsIgnoreCase("none") && itemMaterial == null){
                            player.sendMessage(prefix + "§7The material §8» §b" + itemName + " §cdoesn't exist§7!");
                            return false;
                        }
                        ICloudNPC npc = new SimpleCloudNPC(new CloudNPCMeta(roundLocation.getBlockX(), roundLocation.getBlockY(), roundLocation.getBlockZ(), roundLocation.getWorld().getName(), template.getName(), skinName, itemName, "none", false, false, player.getLocation().getYaw(), player.getLocation().getPitch()));
                        npc.spawn();
                        PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getCloudNPCS().add(npc);
                        PluginBootstrap.getInstance().getNpcService().updateNPCs();
                    }else{
                        sendHelp(player);
                    }
                }else if(args[1].equalsIgnoreCase("gameserver")){
                    String gameServerName = args[2];
                    IGameServer gameServer = PoloCloudAPI.getInstance().getGameServerManager().getCached(gameServerName);
                    if(gameServer == null){
                        player.sendMessage(prefix + "§7The gameserver §8» §b" + gameServerName + " §cisn't online§7!");
                        return false;
                    }
                    if(args[3].equalsIgnoreCase("npc")){
                        String skinName = args[4];
                        String itemName = args[5];
                        Material itemMaterial = Material.getMaterial(itemName);
                        if(!itemName.equalsIgnoreCase("none") && itemMaterial == null){
                            player.sendMessage(prefix + "§7The material §8» §b" + itemName + " §cdoesn't exist§7!");
                            return false;
                        }
                        ICloudNPC npc = new SimpleCloudNPC(new CloudNPCMeta(roundLocation.getBlockX(), roundLocation.getBlockY(), roundLocation.getBlockZ(), roundLocation.getWorld().getName(), gameServer.getName(), skinName, itemName, "none", true, false, player.getLocation().getYaw(), player.getLocation().getPitch()));
                        npc.spawn();
                        PluginBootstrap.getInstance().getNpcService().getCloudNPCManager().getCloudNPCS().add(npc);
                        PluginBootstrap.getInstance().getNpcService().updateNPCs();
                    }else{
                        sendHelp(player);
                    }
                }else{
                    sendHelp(player);
                }
            }else{
                sendHelp(player);
            }
        }else{
            sendHelp(player);
        }
        return false;
    }

    private void sendHelp(Player player){
        String prefix = PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix() + "[NPCs] ";
        player.sendMessage("§7----[§bCloudNPCs§7]----");
        player.sendMessage(prefix + "§7Use §b/cloudnpcs list §7to list all spawned NPCs");
        player.sendMessage(prefix + "§7Use §b/cloudnpcs remove §7to remove the NPC on you current location");
        player.sendMessage(prefix + "§7Use §b/cloudnpcs create gamserver <gameservername> entity <entitytype> §7to create a NPCEntity for a single gameserver");
        player.sendMessage(prefix + "§7Use §b/cloudnpcs create template <templatename> entity <entitytype> §7to create a NPCEntity for a template");
        player.sendMessage(prefix + "§7Use §b/cloudnpcs create gamserver <gameservername> npc <skinname or 'default'> <itemInHand or 'none'> §7to create a NPC for a single gameserver");
        player.sendMessage(prefix + "§7Use §b/cloudnpcs create template <templatename> npc <skinname or 'default'> <itemInHand or 'none'> §7to create a NPC for a template");
    }

}
