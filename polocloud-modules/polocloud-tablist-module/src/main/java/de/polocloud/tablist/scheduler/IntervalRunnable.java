package de.polocloud.tablist.scheduler;

import de.polocloud.tablist.config.TablistConfig;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class IntervalRunnable {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> beeperHandle;

    public IntervalRunnable(TablistConfig tablistConfig) {
        if (!tablistConfig.getTabInterval().isUse()) return;

        int delay = tablistConfig.getTabInterval().getTicks();

        final Runnable beeper = (() -> {

        });
         beeperHandle = scheduler.scheduleAtFixedRate(beeper, delay, delay, TimeUnit.SECONDS);
    }

    public void destroy(){
        beeperHandle.cancel(true);
    }

}

