package de.polocloud.bootstrap.template;

import de.polocloud.api.template.ITemplate;

public class SimpleTemplate implements ITemplate {

    private String name;

    private int maxServerCount;
    private int minServerCount;

    @Override
    public int getMinServerCount() {
        return this.minServerCount;
    }

    @Override
    public int getMaxServerCount() {
        return this.maxServerCount;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
