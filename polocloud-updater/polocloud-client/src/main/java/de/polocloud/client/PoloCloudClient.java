package de.polocloud.client;

public class PoloCloudClient {

    public static String PREFIX = "PoloCloud Client » ";
    private static PoloCloudClient instance;
    private final String url;
    private final int port;

    private final ExceptionReportService exceptionReportService;
    private final ChangelogRequestService changelogRequestService;

    public PoloCloudClient(String url, int port) {
        instance = this;
        this.url = url;
        this.port = port;
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
}
