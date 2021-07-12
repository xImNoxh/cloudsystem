package de.polocloud.bootstrap.config.motd;

public class CloudMotd {

    private boolean use = true;
    private String currentMotdKey[] = new String[]{"default"};
    private MotdObject[] motdList = new MotdObject[]{new MotdObject()};

    public MotdObject[] getMotdList() {
        return motdList;
    }

    public String[] getCurrentMotdKey() {
        return currentMotdKey;
    }

    public boolean isUse() {
        return use;
    }
}
