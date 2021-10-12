package de.polocloud.modules.permission.global.command;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.annotation.CommandExecutors;
import de.polocloud.api.command.annotation.CommandPermission;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.network.protocol.packet.base.response.extra.INetworkPromise;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.util.PoloHelper;
import de.polocloud.modules.permission.global.api.IPermission;
import de.polocloud.modules.permission.global.api.IPermissionGroup;
import de.polocloud.modules.permission.global.api.IPermissionUser;
import de.polocloud.modules.permission.global.api.PermissionPool;
import de.polocloud.modules.permission.global.api.impl.SimplePermissionGroup;
import de.polocloud.modules.permission.global.setup.PermissionGroupSetup;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PermsCommand implements CommandListener {

    @Command(
        name = "permissions",
        aliases = {"perms", "cloudperms", "poloperms"},
        description = "Manages the Permissions module"
    )
    @CommandExecutors(ExecutorType.ALL)
    @CommandPermission("cloud.use")
    public void execute(CommandExecutor executor, String[] fullArgs, String... args) {
        String prefix = PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix(executor);
        String slash = executor instanceof ICloudPlayer ? "/" : "";

        PermissionPool permissionPool = PermissionPool.getInstance();

        if (args.length == 1 && args[0].equalsIgnoreCase("debug") && executor instanceof ICloudPlayer) {
            INetworkPromise<IPermissionUser> promise = permissionPool.getOfflineUser(((ICloudPlayer)executor).getUUID());

            IPermissionUser permissionUser = promise.blocking().get();
            if (permissionUser == null) {
                executor.sendMessage(prefix + "§cNulled user!");
                return;
            }
            executor.sendMessage(prefix + PoloHelper.GSON_INSTANCE.toJson(permissionUser));
            return;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("group")) {
                if (args[1].equalsIgnoreCase("list")) {
                    if (permissionPool.getAllCachedPermissionGroups().isEmpty()) {
                        executor.sendMessage(prefix + "§cUnfortunately there are §eno PermissionGroups §cyet!");
                        return;
                    }
                    executor.sendMessage(prefix + "----[PermissionGroups]----");
                    for (IPermissionGroup group : permissionPool.getAllCachedPermissionGroups()) {
                        executor.sendMessage(prefix + group.getName() + " §8(§7Id: §3" + group.getId() + "§8)");
                    }
                    executor.sendMessage(prefix + "----[/PermissionGroups]----");
                } else if (args[1].equalsIgnoreCase("create")) {
                    if (executor instanceof ICloudPlayer) {
                        executor.sendMessage(prefix + "§cThis can only be executed in the §eMaster-Console§c!");
                        return;
                    }
                    new PermissionGroupSetup().sendSetup();
                } else {
                    sendHelp(executor, prefix);
                }
            } else {
                sendHelp(executor, prefix);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("group")) {
                String name = args[1];
                IPermissionGroup permissionGroup = permissionPool.getCachedPermissionGroup(name);
                if (permissionGroup == null) {
                    executor.sendMessage(prefix + "§cThere is no existing PermissionGroup with the name §e" + name + "§c!");
                    return;
                }
                if (args[2].equalsIgnoreCase("delete")) {
                    permissionPool.deletePermissionGroup(permissionGroup);
                    permissionPool.update();
                    executor.sendMessage(prefix + "§7You §asuccessfully §7deleted group §c" + permissionGroup.getName() + "§8!");

                } else if (args[2].equalsIgnoreCase("info")) {

                    List<String> inheritances = ((SimplePermissionGroup) permissionGroup).getInheritancesStringBased();

                    executor.sendMessage(prefix + "----[PermissionGroup]----");
                    executor.sendMessage(prefix + "§8» §7Name§8: §b" + permissionGroup.getDisplay().getColor().getColor() + permissionGroup.getName());
                    executor.sendMessage(prefix + "§8» §7Priority §8: §b" + permissionGroup.getId());
                    executor.sendMessage(prefix + "§8» §7Color §8: §b" + permissionGroup.getDisplay().getColor().getColor() + permissionGroup.getDisplay().getColor().name());
                    executor.sendMessage(prefix + "§8» §7Prefix §8: §b" + permissionGroup.getDisplay().getPrefix() + "Name");
                    executor.sendMessage(prefix + "§8» §7Suffix §8: §b" + (permissionGroup.getDisplay().getSuffix().trim().isEmpty() ? "§cNone" : permissionGroup.getDisplay().getSuffix()));
                    executor.sendMessage(prefix + "§8» §7ChatFormat §8: §b" + (permissionGroup.getDisplay().getChatFormat().trim().isEmpty() ? "§aDefault" : permissionGroup.getDisplay().getChatFormat()));
                    executor.sendMessage(prefix + "§8» §7Inheritances §8: §b" + (inheritances.isEmpty() ? "§cNone" : inheritances.toString().replace("[", "").replace("]", "")));
                    executor.sendMessage(prefix + "§8» §7Permissions §8: §b" + (permissionGroup.getPermissions().isEmpty() ? "§cNone" : permissionGroup.getPermissions().toString().replace("[", "").replace("]", "")));
                    executor.sendMessage(prefix + "----[/PermissionGroup]----");
                } else {
                    sendHelp(executor, prefix);
                }
            } else if (args[0].equalsIgnoreCase("user")) {
                String name = args[1];
                if (args[2].equalsIgnoreCase("info")) {
                    IPermissionUser permissionUser = permissionPool.getCachedPermissionUser(name);
                    if (permissionUser == null) {
                        executor.sendMessage(prefix + "§cThere is no registered PermissionUser with the name §e" + name + "§c!");
                        return;
                    }

                    executor.sendMessage(prefix + "----[PermissionUser]----");
                    executor.sendMessage(prefix + "§8» §7Name§8: §b" + permissionUser.getName());
                    executor.sendMessage(prefix + "§8» §7UUID§8: §b" + permissionUser.getUniqueId());
                    executor.sendMessage(prefix + "§8» §7Groups§8: " + (permissionUser.getPermissionGroups().isEmpty() ? "§cNone" : ""));
                    permissionUser.getExpiringPermissionGroups().forEach((group, expiring) -> executor.sendMessage(prefix + "  §8- §b" + group.getName() + " §7Expires: " + (expiring == -1 ? "§cNever" : PoloHelper.SIMPLE_DATE_FORMAT.format(new Date(expiring)))));
                    executor.sendMessage(prefix + "§8» §7Exlusive Permissions§8:" + (permissionUser.getExclusivePermissions().isEmpty() ? "§cNone" : ""));
                    for (IPermission permission : permissionUser.getExclusivePermissions()) {
                        executor.sendMessage(prefix + "  §8- §b" + permission.getPermission() + " §7Expires: " + (permission.getExpiringTime() == -1 ? "§cNever" : PoloHelper.SIMPLE_DATE_FORMAT.format(new Date(permission.getExpiringTime()))));
                    }

                    executor.sendMessage(prefix + "----[/PermissionUser]----");
                } else {
                    sendHelp(executor, prefix);
                }
            } else {
                sendHelp(executor, prefix);
            }
        } else if (args.length == 5) {
            if (args[0].equalsIgnoreCase("group")) {
                String name = args[1];
                IPermissionGroup permissionGroup = permissionPool.getCachedPermissionGroup(name);
                if (permissionGroup == null) {
                    executor.sendMessage(prefix + "§cThere is no existing PermissionGroup with the name §e" + name + "§c!");
                    return;
                }
                if (args[2].equalsIgnoreCase("permission")) {
                    if (args[3].equalsIgnoreCase("add")) {
                        String permission = args[4];

                        if (permissionGroup.hasPermission(permission)) {
                            executor.sendMessage(prefix + "§cThe group §e" + permissionGroup.getName() + " §calready has the permission §e" + permission + "§c!");
                            return;
                        }
                        permissionGroup.addPermission(permission);
                        permissionGroup.update();
                        executor.sendMessage(prefix + "§7Successfully added permission §b" + permission + " §7to group §3" + permissionGroup.getName() + "§8!");

                    } else if (args[3].equalsIgnoreCase("remove")) {
                        String permission = args[4];
                        if (!permissionGroup.hasPermission(permission)) {
                            executor.sendMessage(prefix + "§cThe group §e" + permissionGroup.getName() + " §cdoes not have the permission §e" + permission + "§c!");
                            return;
                        }
                        permissionGroup.removePermission(permission);
                        permissionGroup.update();
                        executor.sendMessage(prefix + "§7Successfully removed permission §b" + permission + " §7to group §3" + permissionGroup.getName() + "§8!");
                    } else {
                        executor.sendMessage(prefix + "§c" + slash + "perms group " + permissionGroup.getName() + " permission <add/remove> <permission>");
                    }
                } else {
                    sendHelp(executor, prefix);
                }
            } else if (args[0].equalsIgnoreCase("user")) {
                String name = args[1];
                IPermissionUser permissionUser = permissionPool.getCachedPermissionUser(name);
                if (permissionUser == null) {
                    executor.sendMessage(prefix + "§cThere is no registered PermissionUser with the name §e" + name + "§c!");
                    return;
                }
                if (args[2].equalsIgnoreCase("group")) {

                    String group = args[4];
                    IPermissionGroup permissionGroup = permissionPool.getCachedPermissionGroup(group);
                    if (permissionGroup == null) {
                        executor.sendMessage(prefix + "§cThere is no existing PermissionGroup with the name §e" + group + "§c!");
                        return;
                    }
                    if (args[3].equalsIgnoreCase("remove")) {
                        if (!permissionUser.hasPermissionGroup(permissionGroup)) {
                            executor.sendMessage(prefix + "§cThe user §e" + permissionUser.getName() + "§c does not have the group §e" + group + "§c!");
                            return;
                        }
                        permissionUser.removePermissionGroup(permissionGroup);
                        permissionUser.update();
                        executor.sendMessage(prefix + "§7Successfully removed §b" + permissionUser.getName() + " §7from group §3" + permissionGroup.getName() + "§8!");
                    } else if (args[3].equalsIgnoreCase("add")) {
                        if (permissionUser.hasPermissionGroup(permissionGroup)) {
                            executor.sendMessage(prefix + "§cThe user §e" + permissionUser.getName() + "§c already has the group §e" + group + "§c!");
                            return;
                        }

                        permissionUser.addPermissionGroup(permissionGroup, -1L);
                        permissionUser.update();
                        executor.sendMessage(prefix + "§7Successfully added §b" + permissionUser.getName() + " §7to group §3" + permissionGroup.getName() + " §8(§eLIFETIME§8) §8!");
                    }
                } else if (args[2].equalsIgnoreCase("permission")) {
                    if (args[3].equalsIgnoreCase("remove")) {
                        String perms = args[4];

                        if (permissionUser.getExclusivePermissions().stream().noneMatch(iPermission -> iPermission.getPermission().equalsIgnoreCase(perms))) {
                            executor.sendMessage(prefix + "§cThe player §e" + permissionUser.getName() + " §cdoes not have has the permission §e" + perms + "§c!");
                            return;
                        }
                        permissionUser.removePermission(perms);
                        permissionUser.update();
                        executor.sendMessage(prefix + "§7Successfully removed permission §b" + perms + " §7from User §3" + permissionUser.getName() + "§8!");
                    } else if (args[3].equalsIgnoreCase("add")) {
                        String perms = args[4];

                        if (permissionUser.getExclusivePermissions().stream().anyMatch(iPermission -> iPermission.getPermission().equalsIgnoreCase(perms))) {
                            executor.sendMessage(prefix + "§cThe User §e" + permissionUser.getName() + " §calready has the permission §e" + perms + "§c!");
                            return;
                        }
                        permissionUser.addPermission(perms, -1L);
                        permissionUser.update();
                        executor.sendMessage(prefix + "§7Successfully added permission §b" + perms + " §7to User §3" + permissionUser.getName() + "§8!");
                    }
                } else {
                    sendHelp(executor, prefix);
                }
            } else {
                sendHelp(executor, prefix);
            }
        } else if (args.length == 7 && args[0].equalsIgnoreCase("user")) {
            String name = args[1];
            IPermissionUser permissionUser = permissionPool.getCachedPermissionUser(name);
            if (permissionUser == null) {
                executor.sendMessage(prefix + "§cThere is no registered PermissionUser with the name §e" + name + "§c!");
                return;
            }
            if (args[2].equalsIgnoreCase("group") && args[3].equalsIgnoreCase("add")) {
                String group = args[4];
                IPermissionGroup permissionGroup = permissionPool.getCachedPermissionGroup(group);
                if (permissionGroup == null) {
                    executor.sendMessage(prefix + "§cThere is no existing PermissionGroup with the name §e" + group + "§c!");
                    return;
                }

                if (permissionUser.hasPermissionGroup(permissionGroup)) {
                    executor.sendMessage(prefix + "§cThe user §e" + permissionUser.getName() + "§c already has the group §e" + group + "§c!");
                    return;
                }

                TimeUnit timeUnit;
                try {
                    timeUnit = TimeUnit.valueOf(args[6].toLowerCase().endsWith("s") ? args[6].toUpperCase() : args[6].toUpperCase() + "S");
                } catch (IllegalArgumentException e) {
                    timeUnit = null;
                }

                try {
                    long duration = timeUnit == null ? -1L : (System.currentTimeMillis() + timeUnit.toMillis(Long.parseLong(args[5])));
                    permissionUser.addPermissionGroup(permissionGroup, duration);
                    permissionUser.update();
                    executor.sendMessage(prefix + "§7Successfully added §b" + permissionUser.getName() + " §7to group §3" + permissionGroup.getName() + " §8(§e" + args[5] + " " + timeUnit + "(S)" + "§8) §8!");
                } catch (NumberFormatException e) {
                    executor.sendMessage(prefix + "§cPlease provide a valid §eDuration §clike §e1 DAY§c, §e1 HOUR§c, §e1 WEEK§c!");
                }
            } else if (args[2].equalsIgnoreCase("permission") && args[3].equalsIgnoreCase("add")) {
                String perms = args[4];

                if (permissionUser.getExclusivePermissions().stream().anyMatch(iPermission -> iPermission.getPermission().equalsIgnoreCase(perms))) {
                    executor.sendMessage(prefix + "§cThe User §e" + permissionUser.getName() + " §calready has the permission §e" + perms + "§c!");
                    return;
                }

                TimeUnit timeUnit;
                try {
                    timeUnit = TimeUnit.valueOf(args[6].toLowerCase().endsWith("s") ? args[6].toUpperCase() : args[6].toUpperCase() + "S");
                } catch (IllegalArgumentException e) {
                    timeUnit = null;
                }

                try {

                    long duration = timeUnit == null ? -1L : (System.currentTimeMillis() + timeUnit.toMillis(Long.parseLong(args[5])));
                    permissionUser.addPermission(perms, duration);
                    permissionUser.update();
                    executor.sendMessage(prefix + "§7Successfully added permission §b" + perms + " §7to User §3" + permissionUser.getName() + " for §e" + args[5] + " " + timeUnit + "(S)§8!");

                } catch (NumberFormatException e) {
                    executor.sendMessage(prefix + "§cPlease provide a valid §eDuration §clike §e1 DAY§c, §e1 HOUR§c, §e1 WEEK§c!");
                }
            }
        } else {
            sendHelp(executor, prefix);
        }
    }


    private void sendHelp(CommandExecutor executor, String prefix) {

        String slash = executor instanceof ICloudPlayer ? "/" : "";

        executor.sendMessage(prefix + "----[Permissions]----");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms group list §7Lists all groups");
        if (!(executor instanceof ICloudPlayer)) {
            executor.sendMessage(prefix + "§8» §b" + slash + "perms group create §7Creates a new group");
        }
        executor.sendMessage(prefix + "§8» §b" + slash + "perms group <Group> delete §7Deletes a group");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms group <Group> info §7Displays info about a group");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms group <Group> permission add <Permission> §7Adds a permission to a group");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms group <Group> permission remove <Permission> §7Removes a permission from a group");
        executor.sendMessage("§8");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms user <User> info §7Displays info about a user");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms user <User> group add <Group> <Duration> <TimeUnit> §7Adds a user to a group for a given time");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms user <User> group remove <Group> §7Removes a user from a group");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms user <User> permission add <Permission> (<Duration> <TimeUnit>) §7Adds a permission to a user for a given time");
        executor.sendMessage(prefix + "§8» §b" + slash + "perms user <User> permission remove <Permission> §7Removes a permission from a user");
        executor.sendMessage(prefix + "----[/Permissions]----");

    }
}
