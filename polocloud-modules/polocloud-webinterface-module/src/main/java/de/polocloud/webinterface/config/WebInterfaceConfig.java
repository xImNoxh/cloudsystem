package de.polocloud.webinterface.config;

import de.polocloud.api.config.IConfig;

import java.security.SecureRandom;

public class WebInterfaceConfig implements IConfig {


    private Network network = new Network();
    private Security security = new Security();

    public Network getNetwork() {
        return network;
    }

    public Security getSecurity() {
        return security;
    }

    public static class Security {

        private byte[] salt;

        public void generateNewSalt(){
            salt = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);
        }

        public byte[] getSalt() {
            return salt;
        }
    }

    public static class Network {
        private int port = 6988;

        public int getPort() {
            return port;
        }
    }


}
