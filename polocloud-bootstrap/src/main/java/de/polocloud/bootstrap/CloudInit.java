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

        boolean devMode = false;
        if(args.length == 2){
            if(args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("true")) {
                devMode = Boolean.parseBoolean(args[1]);
            }
        }

        if (args[0].equalsIgnoreCase("master")) {
            PoloHelper.PRE_POLO_TYPE = PoloType.MASTER;
            //start master
            Master master = new Master();
            master.start();


        } else if (args[0].equalsIgnoreCase("wrapper")) {
            PoloHelper.PRE_POLO_TYPE = PoloType.WRAPPER;
            //start wrapper
            Wrapper wrapper = new Wrapper(devMode);
            wrapper.start();
        } else if (args[0].equalsIgnoreCase("both")) {
            PoloHelper.PRE_POLO_TYPE = PoloType.MASTER_AND_WRAPPER;

            Master master = new Master();
            master.start();

            Wrapper wrapper = new Wrapper(devMode);
            wrapper.start();
        }
    }
}
