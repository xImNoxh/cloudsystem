package de.polocloud.api.gameserver.port;


import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.helper.TemplateType;

import java.util.ArrayList;

/**
 * This Service searches for a free Port
 * for a certain group
 * It will look for ports within a specific range of the proxyStartPort or serverStartPort
 */
public class SimpleCachedPortManager implements IPortManager {

    /**
     * The used ports
     */
    private ArrayList<String> portList;

    /**
     * The used proxy ports
     */
    private ArrayList<String> proxyPortList;

    /**
     * The start proxyPort and start serverPort
     */
    private int proxyPort, serverPort;

    public SimpleCachedPortManager(int proxyPort, int serverPort) {
        this.proxyPort = proxyPort;
        this.serverPort = serverPort;
        this.portList = new ArrayList<>();
        this.proxyPortList = new ArrayList<>();
    }

    @Override
    public void removePort(Integer port) {
        this.portList.remove(String.valueOf(port));
        this.proxyPortList.remove(String.valueOf(port));
    }

    @Override
    public int getPort(ITemplate template) {
        return template.getTemplateType() == TemplateType.PROXY ? getFreeProxyPort() : getFreePort();
    }

    @Override
    public int getFreePort() {
        for (int i = this.serverPort; i < (this.serverPort + 300000);) {
            if (this.portList.contains(String.valueOf(i))) {
                i++;
                continue;
            }
            this.portList.add(String.valueOf(i));
            return i;
        }
        return 404;
    }

    @Override
    public int getFreeProxyPort() {
        for (int i = this.proxyPort; i < (this.proxyPort + 300000);) {
            if (this.proxyPortList.contains(String.valueOf(i))) {
                i++;
                continue;
            }
            this.proxyPortList.add(String.valueOf(i));
            return i;
        }
        return 404;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setPortList(ArrayList<String> portList) {
        this.portList = portList;
    }

    public void setProxyPortList(ArrayList<String> proxyPortList) {
        this.proxyPortList = proxyPortList;
    }

    public ArrayList<String> getPortList() {
        return portList;
    }

    public ArrayList<String> getProxyPortList() {
        return proxyPortList;
    }

    @Override
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    @Override
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

}
