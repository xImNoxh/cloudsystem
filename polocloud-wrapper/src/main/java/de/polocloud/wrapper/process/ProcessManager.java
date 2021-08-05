package de.polocloud.wrapper.process;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessManager {

    private Map<Long, Process> processMap = new ConcurrentHashMap<>();

    public void addProcess(long snowFlake, Process process) {
        processMap.put(snowFlake, process);
    }

    public Process getProcess(long snowFlake) {
        return processMap.get(snowFlake);
    }

    public void terminateProcess(long snowFlake) {
        processMap.remove(snowFlake).destroy();
    }

    public void removeProcess(long snowFlake) {
        processMap.remove(snowFlake);
    }


}
