package de.polocloud.bootstrap;

import de.polocloud.bootstrap.logger.LogBootstrapService;
import de.polocloud.wrapper.Wrapper;

public class CloudInit {

    private LogBootstrapService logBootstrapService;

    public CloudInit(String[] args) {

        logBootstrapService = new LogBootstrapService();
        logBootstrapService.printSymbol();

        if (args[0].equalsIgnoreCase("master")) {
            //start master
            System.out.println("bootstrapping Master");
            Master master = new Master();
            master.start();

        } else if (args[0].equalsIgnoreCase("wrapper")) {
            //start wrapper
            System.out.println("bootstrapping Wrapper");

            Wrapper wrapper = new Wrapper();
            wrapper.start();
        }
    }
}
