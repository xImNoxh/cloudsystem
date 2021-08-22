package de.polocloud.bootstrap;

import de.polocloud.bootstrap.logger.LogBootstrapService;
import de.polocloud.wrapper.Wrapper;

public class CloudInit {

    public CloudInit(String[] args) {

        LogBootstrapService logBootstrapService = new LogBootstrapService();
        logBootstrapService.printSymbol();
        
        boolean devMode = false;
        if(args.length == 2){
            if(args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("true")) {
                devMode = Boolean.parseBoolean(args[1]);
            }
        }

        if (args[0].equalsIgnoreCase("master")) {
            //start master
            Master master = new Master();
            master.start();
        } else if (args[0].equalsIgnoreCase("wrapper")) {
            //start wrapper
            Wrapper wrapper = new Wrapper(devMode);
            wrapper.start();
        } else if (args[0].equalsIgnoreCase("both")) {
            Master master = new Master();
            master.start();
            Wrapper wrapper = new Wrapper(devMode);
            wrapper.start();
        }
    }
}
