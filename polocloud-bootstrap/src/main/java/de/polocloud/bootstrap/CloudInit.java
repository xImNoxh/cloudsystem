package de.polocloud.bootstrap;

import de.polocloud.bootstrap.logger.LogBootstrapService;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.reader.ConsoleReadThread;
import de.polocloud.wrapper.Wrapper;

public class CloudInit {

    private LogBootstrapService logBootstrapService;

    public CloudInit(String[] args) {

        logBootstrapService = new LogBootstrapService();
        logBootstrapService.printSymbol();

        new ConsoleReadThread(Logger.getConsoleReader());
        //new ServiceTypeSetup().sendSetup();

        if (args[0].equalsIgnoreCase("master")) {
            //start master
            Master master = new Master();
            master.start();
        } else if (args[0].equalsIgnoreCase("wrapper")) {
            //start wrapper
            Wrapper wrapper = new Wrapper();
            wrapper.start();
        }else if (args[0].equalsIgnoreCase("both")) {
            Master master = new Master();
            master.start();
            Wrapper wrapper = new Wrapper();
            wrapper.start();
        }
    }
}
