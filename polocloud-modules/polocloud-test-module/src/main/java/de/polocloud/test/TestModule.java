package de.polocloud.test;

import de.polocloud.api.module.Module;

public class TestModule extends Module {
    @Override
    public void onLoad() {
        System.out.println("Hello world test Module! :)");

        System.out.println("loaded " + getTemplateService().getLoadedTemplates().size() + " Templates :)");

    }

    @Override
    public void onShutdown() {

    }
}
