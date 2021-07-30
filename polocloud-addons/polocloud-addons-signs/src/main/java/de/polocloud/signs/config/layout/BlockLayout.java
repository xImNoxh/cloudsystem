package de.polocloud.signs.config.layout;

public class BlockLayout {

    private boolean use = true;
    private int id;
    private int subId;

    public BlockLayout(int id, int subId) {
        this.id = id;
        this.subId = subId;
    }

    public int getId() {
        return id;
    }

    public int getSubId() {
        return subId;
    }

    public boolean isUse() {
        return use;
    }
}
