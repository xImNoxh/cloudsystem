package de.polocloud.api.config.master.messages;

public class Messages {

    /**
     * The prefix for all messages
     */
    private final String prefix;

    /**
     * If the proxy is in maintenance
     */
    private final String proxyMaintenanceMessage;

    /**
     * If a group is in maintenance
     */
    private final String groupMaintenanceMessage;

    /**
     * If the network is full
     */
    private final String networkIsFull;

    /**
     * If a server is full
     */
    private final String serviceIsFull;

    /**
     * If no fallback was found
     */
    private final String noFallbackServer;

    /**
     * If the player got kicked and no fallback was found
     */
    private final String kickedAndNoFallbackServer;

    /**
     * If somebody is not joining via proxy
     */
    private final String onlyProxyJoin;

    /**
     * When network is shut down
     */
    private final String networkShutdown;

    public Messages() {
        this.prefix = "§bPoloCloud §7» ";

        this.groupMaintenanceMessage = "§7This §bgroup §7is in maintenance§8...";
        this.proxyMaintenanceMessage = "§7This §bnetwork §7is in maintenance§8...";

        this.networkIsFull = "%prefix% §7The network is §cfull§8...";
        this.serviceIsFull = "%prefix% §7The service is §cfull§8...";

        this.noFallbackServer = "%prefix% §cCould not find a suitable fallback to connect you to!";
        this.kickedAndNoFallbackServer = "%prefix% §cThe server you were on went down, but no fallback server was found!";
        this.onlyProxyJoin = "%prefix% §cYou can only join on a Proxy§8.";
        this.networkShutdown = "%prefix% §cThe network shut down!";
    }


    private String format(String s) {
        return s.replace("&", "§").replace("%prefix%", getPrefix());
    }

    public String getKickedAndNoFallbackServer() {
        return format(kickedAndNoFallbackServer);
    }

    public String getPrefix() {
        return prefix.replace("&", "§");
    }

    public String getNetworkShutdown() {
        return format(networkShutdown);
    }

    public String getProxyMaintenanceMessage() {
        return format(proxyMaintenanceMessage);
    }

    public String getGroupMaintenanceMessage() {
        return format(groupMaintenanceMessage);
    }

    public String getNoFallbackServer() {
        return format(noFallbackServer);
    }

    public String getOnlyProxyJoin() {
        return format(onlyProxyJoin);
    }

    public String getNetworkIsFull() {
        return format(networkIsFull);
    }

    public String getServiceIsFull() {
        return format(serviceIsFull);
    }
}
