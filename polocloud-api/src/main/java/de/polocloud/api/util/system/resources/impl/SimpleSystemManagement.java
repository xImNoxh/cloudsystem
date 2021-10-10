package de.polocloud.api.util.system.resources.impl;

import de.polocloud.api.util.system.resources.ISystemManagement;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleSystemManagement implements ISystemManagement {

    @Override
    public long getTotalClassCount() {
        return ManagementFactory.getClassLoadingMXBean().getTotalLoadedClassCount();
    }

    @Override
    public int getLoadedClassCount() {
        return ManagementFactory.getClassLoadingMXBean().getLoadedClassCount();
    }

    @Override
    public long getUnloadedClassCount() {
        return ManagementFactory.getClassLoadingMXBean().getUnloadedClassCount();
    }

    @Override
    public String getManagementVersion() {
        return System.getProperty("java.version");
    }

    @Override
    public String getVendor() {
        return System.getProperty("java.vendor");
    }

    @Override
    public String getVmName() {
        return System.getProperty("java.vm.name");
    }

    @Override
    public String getVmVendor() {
        return System.getProperty("java.vm.vendor");
    }

    @Override
    public String getVmVersion() {
        return System.getProperty("java.vm.version");
    }

    @Override
    public String getVmSpecName() {
        return System.getProperty("java.specification.name");
    }

    @Override
    public String getVmSpecVendor() {
        return System.getProperty("java.specification.vendor");
    }

    @Override
    public String getVmSpecVersion() {
        return System.getProperty("java.specification.version");
    }

    @Override
    public String getClassPath() {
        return System.getProperty("java.class.path");
    }

    @Override
    public String getLibraryPath() {
        return System.getProperty("java.library.path");
    }

    @Override
    public List<String> getVmArguments() {
        return ManagementFactory.getRuntimeMXBean().getInputArguments();
    }

    @Override
    public Map<String, String> getAllProperties() {
        return System.getProperties().entrySet().stream()
            .collect(Collectors.toMap(k -> (String) k.getKey(), e -> (String) e.getValue()));
    }

    @Override
    public Map<String, String> getAllPropertiesSorted() {
        return System.getProperties().entrySet().stream()
            .collect(Collectors.toMap(k -> (String) k.getKey(), e -> (String) e.getValue()))
            .entrySet().stream().sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, HashMap::new));
    }

    @Override
    public long getStartupTime() {
        return ManagementFactory.getRuntimeMXBean().getStartTime();
    }

    @Override
    public long getUptime() {
        return ManagementFactory.getRuntimeMXBean().getUptime();
    }

    @Override
    public int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    @Override
    public long getTotalThreadCount() {
        return Thread.getAllStackTraces().keySet().size();
    }

    @Override
    public long getLiveThreadCount() {
        return Thread.getAllStackTraces().keySet().stream().filter(Thread::isAlive).count();
    }

    @Override
    public long getDaemonThreadCount() {
        return Thread.getAllStackTraces().keySet().stream().filter(Thread::isDaemon).count();
    }

    @Override
    public String getOsName() {
        return System.getProperty("os.name");
    }

    @Override
    public String getOsArch() {
        return System.getProperty("os.arch");
    }

    @Override
    public String getOsVersion() {
        return System.getProperty("os.version");
    }
}
