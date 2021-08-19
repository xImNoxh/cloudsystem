package de.polocloud.api.scheduler;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PoloRunnable {

    /**
     * The runnable
     */
    private final Runnable runnable;

    /**
     * If the runnable was executed
     */
    private boolean executed;

    /**
     * If the runnable should be executed
     * after the executed was set to true
     */
    private final boolean stopAfterExecute;

    /**
     * All extra runnables
     */
    private final List<Runnable> runnables = new ArrayList<>();

    /**
     * Adds all runnables to the extra runnables
     *
     * @param runnable the runnables
     */
    public void add(Runnable... runnable) {
        this.runnables.addAll(Arrays.asList(runnable));
    }

    public PoloRunnable(Runnable runnable, boolean stopAfterExecute) {
        this.runnable = runnable;
        this.stopAfterExecute = stopAfterExecute;
    }

    /**
     * Runs the runnable and marks it as executed
     */
    public void run() {
        for (Runnable rr : this.runnables) {
            rr.run();
        }
        if (this.executed && this.stopAfterExecute) {
            return;
        }
        this.executed = true;
        this.runnable.run();
    }

}
