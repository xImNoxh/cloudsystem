package de.polocloud.wrapper.impl.config.properties;

import java.io.File;

public class BungeeProperties extends ServiceProperties {

    public BungeeProperties(File directory, int maxplayers, int port, String motd, boolean proxyProtocol, boolean onlineMode) {
        super(directory, "config.yml", port);

        setProperties(new String[]{
            "connection_throttle: 4000",
            "connection_throttle_limit: 3",
            "timeout: 30000",
            "permissions:",
            "  default:",
            "  - bungeecord.command.server",
            "  - bungeecord.command.list",
            "  admin:",
            "  - cloud.use",
            "  - bungeecord.command.alert",
            "  - bungeecord.command.end",
            "  - bungeecord.command.ip",
            "  - bungeecord.command.reload",
            "network_compression_threshold: 256",
            "player_limit: -1",
            "prevent_proxy_connections: false",
            "server_connect_timeout: 5000",
            "remote_ping_timeout: 5000",
            "forge_support: true",
            "remote_ping_cache: -1",
            "log_commands: false",
            "log_pings: false",
            "ip_forward: true",
            "disabled_commands:",
            "- disabledcommandhere",
            "groups:",
            "listeners:",
            "- query_port: 25577",
            "  motd: '" + motd + "'",
            "  tab_list: GLOBAL_PING",
            "  query_enabled: false",
            "  proxy_protocol: " + proxyProtocol,
            "  forced_hosts:",
            "    pvp.md-5.net: pvp",
            "  ping_passthrough: false",
            "  priorities:",
            "  bind_local_address: true",
            "  host: 0.0.0.0:" + port,
            "  max_players: " + maxplayers,
            "  tab_size: 60",
            "  force_default_server: false",
            "online_mode: " + onlineMode,
            "servers:"
        });
        writeFile();
    }
}
