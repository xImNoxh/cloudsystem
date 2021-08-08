package de.polocloud.permission.services.handler.messaging;

import de.polocloud.permission.api.group.IPermissionGroup;
import de.polocloud.permission.api.player.IPermissionPlayer;

public interface IMessageHandler {

    void sendPermissionPlayerInfo(IPermissionPlayer permissionPlayer);

    void sendPermissionGroupInfo(IPermissionGroup permissionGroup);


}
