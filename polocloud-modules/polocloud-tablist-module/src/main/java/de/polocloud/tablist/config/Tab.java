package de.polocloud.tablist.config;

public class Tab {

    private Boolean use = true;

    private String[] header;
    private String[] footer;
    private String[] groups;

    public Tab(String[] header, String[] footer, String... groups) {
        this.header = header;
        this.footer = footer;
        this.groups = groups;
    }

    public String[] getFooter() {
        return footer;
    }

    public String[] getGroups() {
        return groups;
    }

    public Boolean getUse() {
        return use;
    }

    public String[] getHeader() {
        return header;
    }
}
