package de.polocloud.bootstrap;

import de.polocloud.api.util.PoloHelper;

import java.nio.file.Path;

public class Bootstrap {

    /**
     * Creates a new Cloud instance
     * @param args the program arguments
     */
    public static void main(String[] args) throws Exception{
       new CloudInit(args);
    }

}
