package de.polocloud.plugin.commands;

import com.google.common.collect.Lists;

import java.util.List;

public class CommandReader {

    private List<String> allowedCommands;

    public CommandReader() {
        this.allowedCommands = Lists.newArrayList();
    }

    public void setAllowedCommands(List<String> allowedCommands) {
        this.allowedCommands = allowedCommands;
    }

    public List<String> getAllowedCommands() {
        return allowedCommands;
    }
}
