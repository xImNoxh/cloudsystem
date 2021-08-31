package de.polocloud.wrapper.manager.screen.impl;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.executor.ConsoleExecutor;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.logger.PoloLog;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.wrapper.Wrapper;
import de.polocloud.wrapper.manager.screen.IScreen;
import de.polocloud.wrapper.manager.screen.IScreenManager;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class SimpleScreen extends Thread implements IScreen {

    /**
     * The thread for this screen
     */
    private final Thread thread;

    /**
     * The process to get output
     */
    private final Process process;

    /**
     * The directory of the screen
     */
    private final File directory;

    private final long snowflake;

    /**
     * The name of the screen
     */
    private final String serviceName;

    /**
     * The already cached lines
     */
    private final List<String> cachedLines;

    /**
     * If screen is running
     */
    private boolean running;

    /**
     * The printer to print lines
     */
    private IScreenManager screenManager;

    /**
     * The console to display
     */
    private ConsoleExecutor cloudConsole;

    public SimpleScreen(Thread thread, Process process, File serverDir, long snowflake, String serviceName) {
        this.thread = thread;
        this.process = process;
        this.directory = serverDir;
        this.snowflake = snowflake;
        this.serviceName = serviceName;
        this.cachedLines = new LinkedList<>();
        this.running = false;
    }

    /**
     * Starts the screen printing
     * Checks if the current screen is this
     * if (true) > Prints line
     * And caches line
     */
    @Override
    public void run() {
        this.running = true;

        InputStream inputStream = process.getInputStream();
        Scanner reader = new Scanner(inputStream, "UTF-8");
        while (this.running) {
            try {
                String line = reader.nextLine();
                if (line == null) {
                    continue;
                }
                this.cachedLines.add(line);

                //Directly logs server output to log
                if (Wrapper.getInstance().getConfig().isLogServerOutput()) {

                    PoloLogger logger = PoloCloudAPI.getInstance().getLoggerFactory().getLoggerOrCreate("servers/" + getService().getTemplate().getName() + "/");
                    PoloLog log = logger.getLog(serviceName + "#" + snowflake);
                    if (log == null) {
                        log = logger.createLog(serviceName + "#" + snowflake);
                        logger.deleteDefaultLog();
                    }

                    log.saveNextLog().noDisplay().log(LogLevel.INFO, line);
                }

                if (screenManager != null && cloudConsole != null && screenManager.getScreen() != null) {
                    if (screenManager.getScreen().getService().getName().equalsIgnoreCase(this.serviceName)) {
                        PoloLogger.print(LogLevel.INFO, formatLine(line));
                    }

                }
            } catch (NoSuchElementException e) {
                //No line was found in the reader.... ignoring
            }
        }
    }

    @Override
    public IGameServer getService() {
        return PoloCloudAPI.getInstance().getGameServerManager().getCached(this.serviceName);
    }

    @Override
    public void setPrinter(ConsoleExecutor console) {
        this.cloudConsole = console;
        this.screenManager = Wrapper.getInstance().getScreenManager();
    }

    @Override
    public String getScreenName() {
        return this.serviceName;
    }

    @Override
    public Thread getThread() {
        return thread;
    }

    @Override
    public Process getProcess() {
        return process;
    }

    @Override
    public File getDirectory() {
        return directory;
    }

    @Override
    public List<String> getCachedLines() {
        return cachedLines;
    }

    @Override
    public boolean isRunning() {
        return running;
    }


    @Override
    public String formatLine(String input) {
        return "§7[§b" + serviceName + "§7] §7" + input;
    }
}
