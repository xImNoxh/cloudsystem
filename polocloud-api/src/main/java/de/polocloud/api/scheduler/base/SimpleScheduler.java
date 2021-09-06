package de.polocloud.api.scheduler.base;



import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.scheduler.SchedulerFuture;
import de.polocloud.api.scheduler.SchedulerRequest;
import de.polocloud.api.scheduler.helper.PoloRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SimpleScheduler implements Scheduler {

    /**
     * The static instance
     */
    private static SimpleScheduler instance;

    /**
	 * All pending tasks
	 */
	private List<SchedulerFuture> tasks;

    /**
     * If its async or sync
     */
	private boolean async;

    /**
	 * The java timer util
	 */
	private final Timer timer;

	public SimpleScheduler() {
        instance = this;
		this.tasks = new ArrayList<>();
		this.timer = new Timer();
	}

	@Override
	public SchedulerFuture schedule(Runnable task, long delay, long period, long times) {
		return scheduleRepeatingTaskForTimes(task, delay, period, times, this.async);
	}

	@Override
	public SchedulerFuture schedule(Runnable task, long delay, long period) {
		return repeatTask(task, delay, period, this.async);
	}

	@Override
	public SchedulerFuture schedule(Runnable task) {
		SimpleSchedulerFuture future = this.runTask(task, this.async, false);

		new Thread(() -> {
			future.run();
			cancelTask(future);
			Thread.interrupted();
		}, "Scheduler#" + future.getId()).start();

		return future;
	}

    @Override
    public SchedulerFuture schedule(Runnable task, SchedulerRequest request) {
        PoloRunnable poloRunnable = new PoloRunnable(task, true);

        boolean[] b = new boolean[] {false};
        return Scheduler.runtimeScheduler().schedule(() -> {
            if (request.shouldCancel()) {
                poloRunnable.run();
                b[0] = true;
            }
        }, 1L, 1L).cancelIf(() -> b[0]);
    }

    @Override
	public SchedulerFuture schedule(Runnable task, long delay) {
		return delayTask(task, delay, this.async);
	}

	//Helper method to internally run a task
	private SimpleSchedulerFuture runTask(Runnable task, boolean async, boolean multipleTimes) {
		if (task == null) {
			return null;
		}
		SimpleSchedulerFuture future = new SimpleSchedulerFuture(!async, task, generateTaskId(), multipleTimes);
		try {
            this.tasks.add(future);
        } catch (IndexOutOfBoundsException e) {
		    this.tasks = new ArrayList<>();
		    this.tasks.add(future);
        }
		return future;
	}

	//Internal helper method to delay task
	public SchedulerFuture delayTask(Runnable task, long delay, boolean async) {

		SimpleSchedulerFuture future = runTask(task, async, false);

		this.timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				future.run();
				cancelTask(future);
				cancel();
				Thread.interrupted();
			}
		}, delay * 50, 1);

		return future;
	}

	//Helper method to repeat tasks
	private SchedulerFuture repeatTask(Runnable task, long delay, long period, boolean async) {
		SimpleSchedulerFuture future = runTask(task, async, true);

		this.timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
					future.run();
					if (future.isCancelled()) {
						cancelTask(future);
						cancel();
						Thread.interrupted();
					}
			}
		}, delay * 50, period * 50);
        this.async = true;
		return future;
	}

	//Helper method to repeat task for times
	private SchedulerFuture scheduleRepeatingTaskForTimes(Runnable task, long delay, long period, final long times, boolean async) {
		SimpleSchedulerFuture future = runTask(task, async, true);

		this.timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
					future.run();
					if (future.isCancelled() || future.getRunTimes() >= times) {
						cancelTask(future);
						cancel();
						Thread.interrupted();
					}
			}
		}, delay * 50, period * 50);
        this.async = true;
		return future;
	}

    @Override
    public SchedulerFuture getTask(int id) {
        return new LinkedList<>(this.tasks).stream().filter(task -> task != null && task.getId() == id).findFirst().orElse(null);
    }

    @Override @Deprecated
    public void cancelTask(int id) {
        this.cancelTask(this.getTask(id));
    }

    @Override
    public void cancelTask(SchedulerFuture task) {
        List<SchedulerFuture> tasks = new ArrayList<>(this.tasks);
        try {
            if (task != null) {
                task.setCancelled(true);
                tasks.removeIf(task1 -> task.getId() == task1.getId());
                this.tasks = tasks;
            }
        } catch (NullPointerException e) {
            //ignoring
        }
    }

    @Override
    public void cancelAllTasks() {
        for (SchedulerFuture task : this.getTasks()) {
            this.cancelTask(task);
        }
    }

	@Override
	public int generateTaskId() {
		int id = ThreadLocalRandom.current().nextInt();
		if (this.getTask(id) != null) {
			return generateTaskId();
		}
		return id;
	}
	
    /**
     * Returns the instance
     * and if the instance is still null,
     * it will create a scheduler instance and returns it
     *
     * @return the instance
     */
    public static SimpleScheduler getInstance() {
        if (instance == null) {
            instance = new SimpleScheduler();
        }
        return instance;
    }

    @Override
    public List<SchedulerFuture> getTasks() {
        return tasks;
    }
    
    @Override
    public Scheduler async() {
        this.async = true;
        return this;
    }

}
