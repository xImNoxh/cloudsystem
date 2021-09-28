package de.polocloud.api.util.system.resources;

import java.lang.management.OperatingSystemMXBean;

public interface IResourceProvider {

    double getSystemCpuLoad();

    double getProcessCpuLoad();

    long getSystemPhysicalMemory();

    long getSystemFreeMemory();

    long getProcessVirtualMemory();

    long getSystemUsedMemory();

    int getSystemProcessors();


    OperatingSystemMXBean getSystem();

}
