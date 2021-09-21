package de.polocloud.api.config.master.messages;

import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.network.protocol.IProtocolObject;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.player.ICloudPlayer;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

@Getter @Setter
public class Messages implements IProtocolObject {

    /**
     * The prefix for all messages
     */
    private String prefix;

    /**
     * If the proxy is in maintenance
     */
    private String proxyMaintenanceMessage;

    /**
     * If a group is in maintenance
     */
    private String groupMaintenanceMessage;

    /**
     * If the network is full
     */
    private String networkIsFull;

    /**
     * If a server is full
     */
    private String serviceIsFull;

    /**
     * If no fallback was found
     */
    private String noFallbackServer;

    /**
     * If already fallback
     */
    private String alreadyOnFallback;

    /**
     * If the player got kicked and no fallback was found
     */
    private String kickedAndNoFallbackServer;

    /**
     * If somebody is not joining via proxy
     */
    private String onlyProxyJoin;

    /**
     * When network is shut down
     */
    private String networkShutdown;

    /**
     * When you're trying to connect to a server
     * that has a higher protocol than your minecraft version
     */
    private String wrongMinecraftVersion;

    /**
     * If a connection was successful
     */
    private String successfullyConnected;

    public Messages() {
        this.prefix = "§bPoloCloud §7» ";

        this.groupMaintenanceMessage = "§7This §bgroup §7is in maintenance§8...";
        this.proxyMaintenanceMessage = "§7This §bnetwork §7is in maintenance§8...";

        this.networkIsFull = "%prefix% §7The network is §cfull§8...";
        this.serviceIsFull = "%prefix% §7The service is §cfull§8...";

        this.noFallbackServer = "%prefix% §cCould not find a suitable fallback to connect you to!";
        this.kickedAndNoFallbackServer = "%prefix% §cThe server you were on went down, but no fallback server was found!";
        this.alreadyOnFallback = "%prefix% §cYou are already on a Fallback!";

        this.onlyProxyJoin = "%prefix% §cYou can only join on a Proxy§8.";
        this.networkShutdown = "%prefix% §cThe network shut down!";
        this.wrongMinecraftVersion = "%prefix% §7For Server §b%server% §7the version §3%required_version% §7is required §8(§7You§8: §a%your_version%§8)";
        this.successfullyConnected = "%prefix% §7Successfully connected to §a%server%§8!";
    }

    public String getAlreadyOnFallback() {
        return format(alreadyOnFallback);
    }

    public String getWrongMinecraftVersion() {
        return format(wrongMinecraftVersion);
    }

    public String getSuccessfullyConnected() {
        return format(successfullyConnected);
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

    public String getPrefix(CommandExecutor executor) {
        return executor instanceof ICloudPlayer ? getPrefix() : "";
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

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(prefix);
        buf.writeString(proxyMaintenanceMessage);
        buf.writeString(groupMaintenanceMessage);
        buf.writeString(networkIsFull);
        buf.writeString(serviceIsFull);
        buf.writeString(noFallbackServer);
        buf.writeString(kickedAndNoFallbackServer);
        buf.writeString(onlyProxyJoin);
        buf.writeString(networkShutdown);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {

        this.prefix = buf.readString();
        this.proxyMaintenanceMessage = buf.readString();
        this.groupMaintenanceMessage = buf.readString();
        this.networkIsFull = buf.readString();
        this.serviceIsFull = buf.readString();
        this.noFallbackServer = buf.readString();
        this.kickedAndNoFallbackServer = buf.readString();
        this.onlyProxyJoin = buf.readString();
        this.networkShutdown = buf.readString();
    }
}
