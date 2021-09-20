package de.polocloud.client;

import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.console.ConsoleColors;
import de.polocloud.client.config.PoloCloudClientConfig;
import org.fusesource.jansi.Ansi;

import java.io.File;

public class PoloCloudClient {

    public static String PREFIX = Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString() + "PoloCloud " + ConsoleColors.GRAY + "» ";
    private static PoloCloudClient instance;
    private final String url;
    private final int port;

    private PoloCloudClientConfig clientConfig;

    private final ExceptionReportService exceptionReportService;
    private final ChangelogRequestService changelogRequestService;

    public PoloCloudClient(String url, int port) {
        instance = this;
        this.url = url;
        this.port = port;
        IConfigLoader loader = new SimpleConfigLoader();
        IConfigSaver saver = new SimpleConfigSaver();

        this.clientConfig = loader.load(PoloCloudClientConfig.class, new File("client.json"));
        saver.save(clientConfig, new File("client.json"));

        this.exceptionReportService = new ExceptionReportService();
        this.changelogRequestService = new ChangelogRequestService();
    }

    public static PoloCloudClient getInstance() {
        return instance;
    }

    public ExceptionReportService getExceptionReportService() {
        return exceptionReportService;
    }

    public ChangelogRequestService getChangelogRequestService() {
        return changelogRequestService;
    }

    public String getUrl() {
        return url;
    }

    public int getPort() {
        return port;
    }

    public PoloCloudClientConfig getClientConfig() {
        return clientConfig;
    }
}
