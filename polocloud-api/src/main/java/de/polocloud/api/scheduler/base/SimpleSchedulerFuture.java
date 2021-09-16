package de.polocloud.api.scheduler.base;

import de.polocloud.api.scheduler.SchedulerFuture;
import de.polocloud.api.scheduler.SchedulerRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SimpleSchedulerFuture implements SchedulerFuture {

	private static final long serialVersionUID = 8617358757810936900L;
	//Constructor parameters
	private final boolean sync;
	private final Runnable runnable;
	private final int id;
	private final boolean repeating;

	//Non-final fields
	private int runTimes;
	private boolean cancelled;
	private boolean error;

	private final List<Consumer<SchedulerFuture>> taskConsumers;
	private final List<SchedulerRequest> cancelWhens;

    public SimpleSchedulerFuture(boolean sync, Runnable runnable, int id, boolean repeating) {
        this.sync = sync;
        this.runnable = runnable;
        this.id = id;
        this.repeating = repeating;

        this.cancelWhens = new ArrayList<>();
        this.taskConsumers = new ArrayList<>();
    }

    @Override
	public SchedulerFuture addListener(Consumer<SchedulerFuture> consumer) {
		this.taskConsumers.add(consumer);
		return this;
	}

	@Override
	public SchedulerFuture cancelIf(SchedulerRequest booleanRequest) {
		this.cancelWhens.add(booleanRequest);
		return this;
	}

	@Override
	public void run() {
		if (cancelled || error) {
			return;
		}

		for (SchedulerRequest cancelWhen : this.cancelWhens) {
			if (cancelWhen.isAccepted()) {
				this.setCancelled(true);
			}
		}

		this.runTimes++;
		try {
			this.runnable.run();
			for (Consumer<SchedulerFuture> taskConsumer : this.taskConsumers) {
				taskConsumer.accept(this);
			}
		} catch (Exception e) {
			this.error = true;
			e.printStackTrace();
		}
	}

    public void setRunTimes(int runTimes) {
        this.runTimes = runTimes;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    @Override
    public boolean isSync() {
        return sync;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    @Override
    public int getId() {
        return id;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public int getRunTimes() {
        return runTimes;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isError() {
        return error;
    }

    public List<Consumer<SchedulerFuture>> getTaskConsumers() {
        return taskConsumers;
    }

    public List<SchedulerRequest> getCancelWhens() {
        return cancelWhens;
    }
}
