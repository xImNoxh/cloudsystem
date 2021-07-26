package de.polocloud.tablist.config;

import de.polocloud.tablist.config.symbol.TabSymbol;

public class Tab {

    private Boolean use = true;

    private TabSymbol[] tabs;
    private String[] groups;

    public Tab(TabSymbol[] tabs, String... groups) {
        this.tabs = tabs;
        this.groups = groups;
    }

    public String[] getGroups() {
        return groups;
    }

    public Boolean getUse() {
        return use;
    }

    public TabSymbol[] getTabs() {
        return tabs;
    }
}
