package de.polocloud.modules.proxy.config.tablist;

public class TablistConfig {

    private boolean use;

    private String header;
    private String footer;

    public TablistConfig(String header, String footer) {
        this.use = true;
        this.header = header;
        this.footer = footer;
    }

    public boolean isUse() {
        return use;
    }

    public String getHeader() {
        return header;
    }

    public String getFooter() {
        return footer;
    }
}
