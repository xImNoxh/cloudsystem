package de.polocloud.api.network;

/**
 * This interface marks that an object that implements
 * this class is a process that can be terminated
 */
public interface ITerminatable {

    /**
     * @return if termination was successful
     */
    boolean terminate();

}
