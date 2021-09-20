package de.polocloud.client.config;

import de.polocloud.api.config.IConfig;
import de.polocloud.client.enumeration.ReportType;

public class PoloCloudClientConfig implements IConfig {

    private int reportedExceptions = 0;
    private ReportType reportType = ReportType.NONE;


    public int getReportedExceptions() {
        return reportedExceptions;
    }

    public void setReportedExceptions(int reportedExceptions) {
        this.reportedExceptions = reportedExceptions;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }
}
