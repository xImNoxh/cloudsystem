package de.polocloud.signs.signs;

public class ConfigSignLocation {

    private int x,y,z;

    private String world, group;

    public ConfigSignLocation(int x, int y, int z, String world, String group) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
