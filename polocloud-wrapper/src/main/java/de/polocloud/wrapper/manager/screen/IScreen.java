package de.polocloud.wrapper.manager.screen;


import de.polocloud.api.command.executor.ConsoleExecutor;
import de.polocloud.api.gameserver.base.IGameServer;

import java.io.File;
import java.util.List;

public interface IScreen {

    /**
     * The {@link IGameServer} of this screen
     */
    IGameServer getService();

    /**
     * The name of the screen
     */
    String getScreenName();

    /**
     * The {@link Thread} of this screen
     */
    Thread[] getThreads();

    /**
     * The {@link Process} of this screen
     */
    Process getProcess();

    /**
     * The directory of the process
     */
    File getDirectory();

    /**
     * If the screen is still running
     */
    boolean isRunning();

    /**
     * All cached lines of this screen
     */
    List<String> getCachedLines();

    /**
     * Sets the values for this screen to be able
     * to output the console to the printer
     *
     * @param console the console
     */
    void setPrinter(ConsoleExecutor console);

    /**
     * Formats a line with the name of the screen
     *
     * @param input the input
     * @return the formatted line
     */
    String formatLine(String input);

    /**
     * Starts the screen output
     */
    void start();

    /**
     * Stops the screen output
     */
    void stop();
}
