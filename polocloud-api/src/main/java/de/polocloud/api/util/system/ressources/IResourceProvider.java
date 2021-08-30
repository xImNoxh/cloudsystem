package de.polocloud.api.util.system.ressources;

import java.lang.management.OperatingSystemMXBean;

public interface IResourceProvider {

    double getSystemCpuLoad();

    double getProcessCpuLoad();

    long getSystemPhysicalMemory();

    long getSystemFreeMemory();

    long getSystemUsedMemory();

    int getSystemProcessors();

    OperatingSystemMXBean getSystem();

}
