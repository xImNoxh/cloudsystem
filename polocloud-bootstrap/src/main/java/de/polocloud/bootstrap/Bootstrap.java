package de.polocloud.bootstrap;

import de.polocloud.wrapper.Wrapper;

public class Bootstrap {

    public static void main(String[] args) {

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
