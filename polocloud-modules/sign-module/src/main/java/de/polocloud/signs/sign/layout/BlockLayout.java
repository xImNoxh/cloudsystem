package de.polocloud.signs.sign.layout;

public class BlockLayout {

    private final boolean use = true;
    private final int id;
    private final int subId;

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
