package de.polocloud.api.scheduler;


import java.util.List;

public interface Scheduler {

    /**
     * Gets the {@link Scheduler} instance if set yet
     *
     * @return instance
     */
    static Scheduler runtimeScheduler() {
        return SimpleScheduler.getInstance();
    }

    /**
     * Gets a {@link SchedulerFuture} by its id
     *
     * @param id the id to search for
     * @return task or null if not found
     */
    SchedulerFuture getTask(int id);

    /**
     * Gets a list of all pending {@link SchedulerFuture}s
     *
     * @return list of tasks
     */
    List<SchedulerFuture> getTasks();

    /**
     * Cancels a task by its id
     *
     * @param id the id
     * @deprecated use {@link Scheduler#cancelTask(SchedulerFuture)}
     */
    @Deprecated
    void cancelTask(int id);

    /**
     * Marks the next task as async
     * and then switches back to sync
     *
     * @return current scheduler
     */
    Scheduler async();

    /**
     * Cancels a task
     *
     * @param task the task to cancel
     */
    void cancelTask(SchedulerFuture task);

    /**
     * Cancels all tasks
     */
    void cancelAllTasks();

    /**
     * Repeats a task for a given amount of time
     * This is executed synchronously
     *
     * @param task the runnable task
     * @param delay the delay between every execution
     * @param period the period
     * @param times the amount of times
     * @return task
     */
    SchedulerFuture schedule(Runnable task, long delay, long period, long times);

    /**
     * Repeats a task
     * This is executed synchronously
     *
     * @param task the runnable task
     * @param delay the delay between every execution
     * @param period the period
     * @return task
     */
    SchedulerFuture schedule(Runnable task, long delay, long period);

    /**
     * Runs a task
     * This is executed synchronously
     *
     * @param task the runnable task
     * @return scheduled task
     */
    SchedulerFuture schedule(Runnable task);

    /**
     * Executes a task ONCE when accepted
     *
     * @param task the task to run
     * @param request the request
     */
    SchedulerFuture schedule(Runnable task, SchedulerRequest request);

    /**
     * Delays a task
     * This is executed synchronously
     *
     * @param task the task
     * @param delay the delay as Minecraft-Ticks (20 ticks = 1 Second)
     * @return scheduled task
     */
    SchedulerFuture schedule(Runnable task, long delay);

    /**
     * Searches for a free task id
     * If the id is already in use, it will generate a new one
     * until one id is free to use
     *
     * @return id as int
     */
    int generateTaskId();

}
