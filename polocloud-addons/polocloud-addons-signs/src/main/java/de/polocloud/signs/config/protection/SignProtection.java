package de.polocloud.signs.config.protection;

public class SignProtection {

    private boolean use = true;

    private int multiply = 2;
    private double distance = 2;

    private int scanInterval = 5;

    private String permission = "cloud.signbypass";

    public boolean isUse() {
        return use;
    }

    public int getMultiply() {
        return multiply;
    }

    public double getDistance() {
        return distance;
    }

    public String getPermission() {
        return permission;
    }

    public int getScanInterval() {
        return scanInterval;
    }
}
