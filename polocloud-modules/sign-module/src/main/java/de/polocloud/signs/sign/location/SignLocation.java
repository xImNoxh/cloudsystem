package de.polocloud.signs.sign.location;

public class SignLocation {

    private int x, y, z;
    private String world, template;

    public SignLocation(int x, int y, int z, String world, String template) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.template = template;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
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

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

}
