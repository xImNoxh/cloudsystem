package de.polocloud.permission.api.database;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nico_ND1
 */
public class DatabaseSQL implements Database {
    private static final AtomicInteger POOL_COUNTER = new AtomicInteger(0);
    // https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing
    private static final int MAXIMUM_POOL_SIZE = (Runtime.getRuntime().availableProcessors() * 2) + 1;
    private static final int MINIMUM_IDLE = Math.min(MAXIMUM_POOL_SIZE, 10);
    private static final long MAX_LIFETIME = TimeUnit.MINUTES.toMillis(30);
    private static final long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
    private static final long LEAK_DETECTION_THRESHOLD = TimeUnit.SECONDS.toMillis(10);

    private HikariDataSource source;

    @Override
    public void connect(@NotNull DatabaseCredentials credentials, String usage) {
        Preconditions.checkArgument(!isConnected(), "Can't override existing connection");

        final HikariConfig hikari = new HikariConfig();

        hikari.setPoolName("helper-sql-" + (usage == null ? POOL_COUNTER.incrementAndGet() : usage + "-" + POOL_COUNTER.incrementAndGet()));

        hikari.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikari.setJdbcUrl("jdbc:mysql://" + credentials.getHostname() + ":" + credentials.getPort() + "/" + credentials.getDatabase());

        hikari.setUsername(credentials.getUsername());
        hikari.setPassword(credentials.getPassword());

        hikari.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
        hikari.setMinimumIdle(MINIMUM_IDLE);

        hikari.setMaxLifetime(MAX_LIFETIME);
        hikari.setConnectionTimeout(CONNECTION_TIMEOUT);
        hikari.setLeakDetectionThreshold(LEAK_DETECTION_THRESHOLD);

        Map<String, String> properties = ImmutableMap.<String, String>builder()
            // Ensure we use utf8 encoding
            .put("useUnicode", "true")
            .put("characterEncoding", "utf8")

            // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
            .put("cachePrepStmts", "true")
            .put("prepStmtCacheSize", "250")
            .put("prepStmtCacheSqlLimit", "2048")
            .put("useServerPrepStmts", "true")
            .put("useLocalSessionState", "true")
            .put("rewriteBatchedStatements", "true")
            .put("cacheResultSetMetadata", "true")
            .put("cacheServerConfiguration", "true")
            .put("elideSetAutoCommits", "true")
            .put("maintainTimeStats", "false")
            .put("alwaysSendSetIsolation", "false")
            .put("cacheCallableStmts", "true")

            // Set the driver level TCP socket timeout
            // See: https://github.com/brettwooldridge/HikariCP/wiki/Rapid-Recovery
            .put("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)))
            .build();

        for (Map.Entry<String, String> property : properties.entrySet()) {
            hikari.addDataSourceProperty(property.getKey(), property.getValue());
        }

        this.source = new HikariDataSource(hikari);
    }

    public Connection getConnection() throws SQLException {
        Preconditions.checkArgument(isConnected());

        return source.getConnection();
    }

    @Override
    public boolean isConnected() {
        return source != null && source.isRunning();
    }

    public void close() {
        Preconditions.checkArgument(source != null);

        source.close();
        source = null;
    }
}
