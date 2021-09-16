package de.polocloud.api.scheduler;

/**
 * This class is used for example to check if some operation is allowed to return then the runnable gets executed
 * this is useful to prevent {@link NullPointerException} and execute the given runnable
 * first if the value is not null
 */
public interface SchedulerRequest {

    /**
     * Checks if the given task should be cancelled
     *
     * @return boolean
     */
    boolean isAccepted();

}
