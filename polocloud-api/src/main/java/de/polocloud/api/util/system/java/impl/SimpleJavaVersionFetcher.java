package de.polocloud.api.util.system.java.impl;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.util.system.java.IJavaVersionFetcher;
import de.polocloud.api.util.system.java.version.JavaVersion;

public class SimpleJavaVersionFetcher implements IJavaVersionFetcher {

    public boolean isEqual(String required, String current){
        JavaVersion requiredJavaVersion = new JavaVersion(required);
        JavaVersion currentJavaVersion = new JavaVersion(current);
        return requiredJavaVersion.equals(currentJavaVersion);
    }

    public boolean isEqualCurrent(String required){
        return new JavaVersion(required).equals(new JavaVersion(PoloCloudAPI.getInstance().getSystemManager().getSystemManagement().getManagementVersion()));
    }

    public boolean isAbove(String required, String current){
        JavaVersion requiredJavaVersion = new JavaVersion(required);
        JavaVersion currentJavaVersion = new JavaVersion(current);
        return requiredJavaVersion.compareTo(currentJavaVersion) > 0;
    }
    public boolean isAboveCurrent(String required){
        JavaVersion requiredJavaVersion = new JavaVersion(required);
        JavaVersion currentJavaVersion = new JavaVersion(PoloCloudAPI.getInstance().getSystemManager().getSystemManagement().getManagementVersion());
        return requiredJavaVersion.compareTo(currentJavaVersion) > 0;
    }

    public boolean isBelow(String required, String current){
        JavaVersion requiredJavaVersion = new JavaVersion(required);
        JavaVersion currentJavaVersion = new JavaVersion(current);
        return requiredJavaVersion.compareTo(currentJavaVersion) < 0;
    }
    public boolean isBelowCurrent(String required){
        JavaVersion requiredJavaVersion = new JavaVersion(required);
        JavaVersion currentJavaVersion = new JavaVersion(PoloCloudAPI.getInstance().getSystemManager().getSystemManagement().getManagementVersion());
        return requiredJavaVersion.compareTo(currentJavaVersion) < 0;
     }
}
