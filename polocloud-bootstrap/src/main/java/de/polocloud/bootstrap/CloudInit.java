package de.polocloud.bootstrap;

import de.polocloud.api.common.PoloType;
import de.polocloud.api.util.gson.PoloHelper;
import de.polocloud.api.console.ConsoleRunner;
import de.polocloud.wrapper.Wrapper;


public class CloudInit {

    /**
     * Launches the cloud with the different program arguments
     * @param args the program arguments (master, wrapper, etc.)
     */
    public CloudInit(String[] args) {

        ConsoleRunner thread = new ConsoleRunner();

        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();

        PoloHelper.clearConsole();

        boolean ignoreUpdater = false;
        boolean devMode = false;

        PoloType poloType = PoloType.NONE;

        for (String arg : args) {
            if (arg.equalsIgnoreCase("--devMode")) {
                devMode = true;
            } else if (arg.equalsIgnoreCase("--ignoreUpdater")) {
                ignoreUpdater = true;
            } else {
                if (arg.equalsIgnoreCase("master")) {
                    poloType = PoloType.MASTER;
                } else if (arg.equalsIgnoreCase("wrapper")) {
                    poloType = PoloType.WRAPPER;
                } else if (arg.equalsIgnoreCase("both")) {
                    poloType = PoloType.MASTER_AND_WRAPPER;
                }
            }
        }

        if (poloType == PoloType.MASTER) {
            //start master
            Master master = new Master();
            PoloHelper.printHeader();
            master.start();
        } else if (poloType == PoloType.WRAPPER) {
            //start wrapper
            Wrapper wrapper = new Wrapper(devMode, ignoreUpdater);
            PoloHelper.printHeader();
            wrapper.start();
        } else if (poloType == PoloType.MASTER_AND_WRAPPER) {

            Master master = new Master();
            PoloHelper.printHeader();
            master.start();

            Wrapper wrapper = new Wrapper(devMode, ignoreUpdater);
            wrapper.start();
        } else {
            System.out.println("Please provide any arguments after '-jar' and tell what process you want to start (Master, Wrapper, Both)");
            System.exit(0);
        }

    }
}
