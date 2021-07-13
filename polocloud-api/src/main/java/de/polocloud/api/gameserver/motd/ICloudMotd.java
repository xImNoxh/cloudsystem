package de.polocloud.api.gameserver.motd;

public interface ICloudMotd {

     IMotdObject[] getMotdList();

     String[] getCurrentMotdKey();

     boolean isUse();
}
