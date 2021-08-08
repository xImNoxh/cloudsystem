package de.polocloud.permission.services.handler.messaging;

import de.polocloud.permission.api.group.IPermissionGroup;
import de.polocloud.permission.api.player.IPermissionPlayer;
import de.polocloud.permission.api.player.PlayerGroupInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MessageHandler implements IMessageHandler {

    /*
    private IMessageChannel<PermissionPlayerInfoMessage> playerChannel;
    private IMessageChannel<PermissionGroupInfoMessage> groupChannel;

     */

    public MessageHandler() {
        /*
        playerChannel = CloudAPI.getInstance().getMessageChannelManager().registerMessageChannel(CloudAPI.getInstance().getThisSidesCloudModule(), "syncPermissionPlayer", PermissionPlayerInfoMessage.class);
        playerChannel.registerListener(((message, server) -> {
            IPermissionPlayer permissionPlayer = Permissions.getInstance().getPermissionPlayerHandler().getCachedPermissionPlayer(message.getUUID());

            Set<PlayerGroupInfo> infos = new HashSet<>();
            for (String s : message.getPlayerGroupInfos().keySet())
                infos.add(new PlayerGroupInfo(Permissions.getInstance().getPermissionGroupHandler().getPermissionGroupByName(s), message.getPlayerGroupInfos().get(s)));

            Permissions.getInstance().getPermissionPlayerHandler().update(permissionPlayer, message.getPermissions(), infos);
            System.out.println(message.getUUID().toString() + " -> successfully received player data update from " + server.getName());
            if(Permissions.getInstance().isProxy()) return;

            PermissionPlayerUpdatedEvent event = new PermissionPlayerUpdatedEvent(Permissions.getInstance().getPermissionPlayerHandler().getCachedPermissionPlayer(permissionPlayer.getUniqueId()));
            Bukkit.getPluginManager().callEvent(event);
        }));

        groupChannel = CloudAPI.getInstance().getMessageChannelManager().registerMessageChannel(CloudAPI.getInstance().getThisSidesCloudModule(), "syncPermissionGroup", PermissionGroupInfoMessage.class);
        groupChannel.registerListener(((message, server) -> {
            IPermissionGroup permissionGroup = Permissions.getInstance().getPermissionGroupHandler().getPermissionGroupByName(message.getName());

            Set<IPermissionGroup> list = new HashSet<>();
            for (String s : message.getGroups())
                list.add(Permissions.getInstance().getPermissionGroupHandler().getPermissionGroupByName(s));

            Permissions.getInstance().getPermissionGroupHandler().update(permissionGroup, message.getPermissions(), list);
            System.out.println(message.getName() + " -> successfully received group data update from " + server.getName());

            if(Permissions.getInstance().isProxy()) return;

            PermissionGroupUpdatedEvent event = new PermissionGroupUpdatedEvent(Permissions.getInstance().getPermissionGroupHandler().getPermissionGroupByName(permissionGroup.getName()));
            Bukkit.getPluginManager().callEvent(event);
        }));

         */


    }

    @Override
    public void sendPermissionPlayerInfo(IPermissionPlayer permissionPlayer) {
        Map<String, Long> map = new HashMap<>();
        for (PlayerGroupInfo info : permissionPlayer.getAllNotExpiredPlayerGroupInfos()) {
            map.put(info.getPermissionGroup().getName(), info.getExpireTime());
        }
        PermissionPlayerInfoMessage message = new PermissionPlayerInfoMessage(permissionPlayer.getUniqueId(), permissionPlayer.getAllNotExpiredPermissions(), map);

        /*
        if (CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(permissionPlayer.getUniqueId()) == null)
            return;

        List<ICloudService> list = CloudAPI.getInstance().getCloudServiceManager().getAllCachedObjects().stream().filter(s -> s.getOnlinePlayers().getBlockingOrNull().stream().anyMatch(player -> player.getUniqueId().equals(permissionPlayer.getUniqueId()))).collect(Collectors.toList());
        playerChannel.sendMessage(message, list);
        for (ICloudService service : list)
            System.out.println(message.getUUID().toString() + " -> send player data to " + service.getName());

         */
    }

    @Override
    public void sendPermissionGroupInfo(IPermissionGroup permissionGroup) {
        PermissionGroupInfoMessage message = new PermissionGroupInfoMessage(permissionGroup.getName(), permissionGroup.getAllNotExpiredPermissions(), permissionGroup.getAllInheritedPermissionGroups().stream().map(IPermissionGroup::getName).collect(Collectors.toSet()));

        /*
        groupChannel.sendMessage(message, CloudAPI.getInstance().getCloudServiceManager().getAllCachedObjects());
        System.out.println(message.getName() + " -> send group data to all servers");

         */
    }


}
