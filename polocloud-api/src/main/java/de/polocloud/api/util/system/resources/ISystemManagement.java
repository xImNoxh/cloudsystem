package de.polocloud.api.util.system.resources;

import java.util.List;
import java.util.Map;

public interface ISystemManagement {

    long getTotalClassCount();
    int getLoadedClassCount();
    long getUnloadedClassCount();
    long getStartupTime();
    long getUptime();
    int getAvailableProcessors();
    long getTotalThreadCount();
    long getLiveThreadCount();
    long getDaemonThreadCount();

    String getManagementVersion();
    String getVendor();
    String getVmName();
    String getVmVendor();
    String getVmVersion();
    String getVmSpecName();
    String getVmSpecVendor();
    String getVmSpecVersion();
    String getOsName();
    String getOsArch();
    String getOsVersion();
    String getClassPath();
    String getLibraryPath();
    List<String> getVmArguments();
    Map<String, String> getAllProperties();
    Map<String, String> getAllPropertiesSorted();

}
