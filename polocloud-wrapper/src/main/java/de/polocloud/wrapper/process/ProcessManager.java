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
        Process remove = processMap.remove(snowFlake);
        if (remove != null) {
            remove.destroy();
        }
    }

    public void removeProcess(long snowFlake) {
        processMap.remove(snowFlake);
    }

    public Map<Long, Process> getProcessMap() {
        return processMap;
    }
}
