package de.polocloud.api.gameserver.port;


import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.helper.TemplateType;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * This Service searches for a free Port
 * for a certain group
 * It will look for ports within a specific range of the proxyStartPort or serverStartPort
 */
public class SimpleCachedPortManager implements IPortManager, Serializable {

    private static final long serialVersionUID = -6055013827003323170L;

    /**
     * The used ports
     */
    private final List<String> portList;

    /**
     * The used proxy ports
     */
    private final List<String> proxyPortList;

    /**
     * The start proxyPort and start serverPort
     */
    private int proxyPort, serverPort;

    public SimpleCachedPortManager(int proxyPort, int serverPort) {
        this.proxyPort = proxyPort;
        this.serverPort = serverPort;
        this.portList = new LinkedList<>();
        this.proxyPortList = new LinkedList<>();
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

    @Override
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    @Override
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

}
