package de.polocloud.bootstrap.config.messages;

public class Messages {

    private String proxyMaintenanceMessage = "§7This §b§lnetwork §7is in maintenance§8...";
    private String groupMaintenanceMessage = "§7This §b§lgroup §7is in maintenance§8...";
    private String networkIsFull = "§7The network is §cfull§8...";
    private String serviceIsFull = "§7The service is §cfull§8...";
    private String noFallbackServer = "§cNo fallback server found§8.";
    private String onlyProxyJoin = "§cYou can only join on a Proxy§8.";

    public String getProxyMaintenanceMessage() {
        return proxyMaintenanceMessage;
    }

    public String getGroupMaintenanceMessage() {
        return groupMaintenanceMessage;
    }

    public String getNoFallbackServer() {
        return noFallbackServer;
    }

    public String getOnlyProxyJoin() {
        return onlyProxyJoin;
    }

    public String getNetworkIsFull() {
        return networkIsFull;
    }

    public String getServiceIsFull() {
        return serviceIsFull;
    }
}
