package de.polocloud.modules.proxy.pluginside.command;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.annotation.CommandExecutors;
import de.polocloud.api.command.annotation.CommandPermission;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ConsoleExecutor;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.modules.proxy.ProxyModule;
import de.polocloud.modules.proxy.api.ProxyConfig;
import de.polocloud.modules.proxy.api.notify.NotifyConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProxyCommand implements CommandListener {

    @Command(
        name = "proxy",
        aliases = {"pr", "cloudproxy"},
        description = "Manages the Proxy module"
    )
    @CommandExecutors(ExecutorType.ALL)
    @CommandPermission("cloud.proxy")
    public void execute(CommandExecutor executor, String[] fullArgs, String... args) {

        String prefix = PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix();
        ProxyConfig proxyConfig = ProxyModule.getProxyModule().getProxyConfig();
        if (proxyConfig == null) {
            executor.sendMessage(prefix + "§cNo §eProxyConfig §cwas found!");
            return;
        }
        List<String> whitelist = proxyConfig.getWhiteListedPlayers();

        if (executor instanceof ConsoleExecutor) {
            prefix = "";
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("whitelist")) {

                List<String> whitelistPlayers = ProxyModule.getProxyModule().getProxyConfig().getWhiteListedPlayers();

                if (whitelistPlayers.isEmpty()) {
                    executor.sendMessage(prefix + "§cIt seems like there are no whitelisted players yet!");
                    return;
                }

                executor.sendMessage(prefix + "----[Whitelist]----");
                for (String whitelistPlayer : whitelistPlayers) {
                    executor.sendMessage(prefix + "§b" + whitelistPlayer);
                }
                executor.sendMessage(prefix + "----[/Whitelist]----");

            } else {
                sendHelp(executor);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("whitelist")) {

                if (args[1].equalsIgnoreCase("reset") || args[1].equalsIgnoreCase("clear")) {
                    proxyConfig.setWhiteListedPlayers(new ArrayList<>());
                    proxyConfig.update();
                    executor.sendMessage(prefix + "§7You §acleared §7the whitelist§8!");
                } else {
                    sendHelp(executor);
                }

            } else if (args[0].equalsIgnoreCase("toggle")) {
                if (args[1].equalsIgnoreCase("maintenance")) {

                    IGameServer thisService = PoloCloudAPI.getInstance().getGameServerManager().getThisService();
                    ITemplate template = thisService.getTemplate();

                    boolean newState = !template.isMaintenance();

                    template.setMaintenance(newState);
                    template.update();

                    executor.sendMessage(prefix + "§7Changed §bmaintenance-state §7to " + (newState ? "§aOn" : "§cOff") + "§8!");

                } else if (args[1].equalsIgnoreCase("notify")) {

                    if (executor instanceof ICloudPlayer) {
                        ICloudPlayer player = (ICloudPlayer) executor;

                        if (!proxyConfig.toggleNotify(player.getUUID())) {
                            executor.sendMessage(prefix + "§7You will now §areceive §7Notify-Messages§8!");
                        } else {
                            executor.sendMessage(prefix + "§7You will from now on §cnot receive §7Notify-Messages§8!");
                        }
                        proxyConfig.update();
                    } else {
                        executor.sendMessage(prefix + "§cThis command only works for §eCloudPlayers§c!");
                    }
                } else {
                    sendHelp(executor);
                }
            } else {
                sendHelp(executor);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("whitelist")) {
                if (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove")) {

                    String player = args[2];

                    if (args[1].equalsIgnoreCase("add")) {
                        if (whitelist.contains(player)) {
                            executor.sendMessage(prefix + "§cThe player §e" + player + " §cis already whitelisted!");
                            return;
                        }
                        whitelist.add(player);
                        proxyConfig.setWhiteListedPlayers(whitelist);
                        proxyConfig.update();
                        executor.sendMessage(prefix + "§7You §aadded §7the player §b" + player + " §7to maintenance§8!");
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        if (!whitelist.contains(player)) {
                            executor.sendMessage(prefix + "§cThe player §e" + player + " §cis not whitelisted yet!");
                            return;
                        }
                        whitelist.remove(player);
                        proxyConfig.setWhiteListedPlayers(whitelist);
                        proxyConfig.update();
                        executor.sendMessage(prefix + "§7You §cremoved §7the player §b" + player + " §7from maintenance§8!");
                    }
                } else {
                    sendHelp(executor);
                }
            } else {
                sendHelp(executor);
            }
        } else {
            sendHelp(executor);
        }

    }


    private void sendHelp(CommandExecutor executor) {

        String prefix = PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix();
        executor.sendMessage(prefix + "----[Proxy]----");
        executor.sendMessage(prefix + "§8» §b/proxy toggle maintenance §7Toggles maintenance");
        executor.sendMessage(prefix + "§8» §b/proxy toggle notify §7Toggles receiving notify messages");
        executor.sendMessage(prefix + "§8» §b/proxy whitelist add <Player> §7Adds a player to the maintenance");
        executor.sendMessage(prefix + "§8» §b/proxy whitelist remove <Player> §7Removes player from maintenance");
        executor.sendMessage(prefix + "§8» §b/proxy whitelist reset §7Clears the whitelist");
        executor.sendMessage(prefix + "§8» §b/proxy whitelist §7Lists all whitelisted players");
        executor.sendMessage(prefix + "----[/Proxy]----");
    }
}
