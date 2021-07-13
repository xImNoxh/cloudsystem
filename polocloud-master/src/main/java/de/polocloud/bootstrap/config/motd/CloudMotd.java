package de.polocloud.bootstrap.config.motd;

import de.polocloud.api.gameserver.motd.ICloudMotd;
import de.polocloud.api.gameserver.motd.IMotdObject;

public class CloudMotd implements ICloudMotd {

    private boolean use = true;
    private String currentMotdKey[] = new String[]{"default"};
    private MotdObject[] motdList = new MotdObject[]{new MotdObject()};

    @Override
    public IMotdObject[] getMotdList() {
        return motdList;
    }

    @Override
    public String[] getCurrentMotdKey() {
        return currentMotdKey;
    }

    @Override
    public boolean isUse() {
        return use;
    }
}
