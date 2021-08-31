package de.polocloud.api.util.system.ressources.impl;

import com.sun.management.OperatingSystemMXBean;
import de.polocloud.api.util.system.ressources.IResourceProvider;

import java.lang.management.ManagementFactory;

public class SimpleResourceProvider implements IResourceProvider {

    @Override
    public double getSystemCpuLoad() {
        return getSystem().getSystemCpuLoad() * 100;
    }

    @Override
    public double getProcessCpuLoad() {
        return getSystem().getProcessCpuLoad() * 100;
    }

    @Override
    public long getSystemPhysicalMemory() {
        return getSystem().getTotalPhysicalMemorySize();
    }

    @Override
    public long getSystemFreeMemory() {
        return getSystem().getFreePhysicalMemorySize();
    }

    @Override
    public long getSystemUsedMemory() {
        return getSystemPhysicalMemory() - getSystemFreeMemory();
    }

    @Override
    public int getSystemProcessors() {
        return getSystem().getAvailableProcessors();
    }

    @Override
    public OperatingSystemMXBean getSystem() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean());
    }
}
