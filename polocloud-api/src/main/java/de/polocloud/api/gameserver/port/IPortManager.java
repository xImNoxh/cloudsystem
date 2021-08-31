package de.polocloud.api.gameserver.port;

import de.polocloud.api.template.base.ITemplate;

public interface IPortManager {

    /**
     * Marks port as unused
     *
     * @param port the port
     */
    void removePort(Integer port);

    /**
     * Automatically decides depending on the {@link ITemplate}
     * if {@link IPortManager#getFreePort()} or {@link IPortManager#getFreeProxyPort()}
     * needs to be called in order to provide the right and free port
     *
     * @param template the template
     * @return port for template
     */
    int getPort(ITemplate template);

    /**
     * Returns free port for server
     *
     * @return free port
     */
    int getFreePort();

    /**
     * Returns free port for proxy
     *
     * @return free proxy port
     */
    int getFreeProxyPort();

    /**
     * Sets the port for proxy groups to start scanning at
     *
     * @param proxyPort the port
     */
    void setProxyPort(int proxyPort);

    /**
     * Sets the port for server groups to start scanning at
     *
     * @param serverPort the port
     */
    public void setServerPort(int serverPort);
}
