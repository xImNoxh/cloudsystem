package de.polocloud.api.util.system.java;

public interface IJavaVersionFetcher {

    boolean isEqual(String required, String current);

    boolean isEqualCurrent(String required);

    boolean isAbove(String required, String current);

    boolean isAboveCurrent(String required);

    boolean isBelow(String required, String current);

    boolean isBelowCurrent(String required);

}
