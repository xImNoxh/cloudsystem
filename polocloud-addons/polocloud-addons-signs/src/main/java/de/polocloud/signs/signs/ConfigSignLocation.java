package de.polocloud.signs.signs;

public class ConfigSignLocation {

    private double x,y,z;

    private String world, group;

    public ConfigSignLocation(double x, double y, double z, String world, String group) {
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

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
