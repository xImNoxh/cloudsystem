package de.polocloud.api.network.helper;

/**
 * This interface marks that an object that implements
 * this class is a process that can be started
 */
public interface IStartable {

    /**
     * Starts the process
     */
    void start();

}
