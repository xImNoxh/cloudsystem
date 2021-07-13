package de.polocloud.api.gameserver.motd;

public interface IMotdObject {

    String getKey();

    String getPlayerInfo();

    String getFirstLine();

    String getSecondLine();
}
