package de.polocloud.proxy.config.whitelist;

import com.google.common.collect.Lists;
import de.polocloud.api.player.ICloudPlayer;

import java.util.List;

public class Whitelist {

    private boolean use = true;
    private List<WhitelistUser> users = Lists.newArrayList();

    private String commandAlias = "cloudwhitelist";
    private String commandPermission = "cloud.whitelist";

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }

    public List<WhitelistUser> getUsers() {
        return users;
    }

    public void setUsers(List<WhitelistUser> users) {
        this.users = users;
    }

    public String getCommandAlias() {
        return commandAlias;
    }

    public void setCommandAlias(String commandAlias) {
        this.commandAlias = commandAlias;
    }

    public String getCommandPermission() {
        return commandPermission;
    }

    public void setCommandPermission(String commandPermission) {
        this.commandPermission = commandPermission;
    }
}
