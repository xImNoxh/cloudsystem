package de.polocloud.test;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.module.Module;

public class TestModule extends Module {


    @Override
    public void onLoad() {
        System.out.println("loaded " + getTemplateService().getLoadedTemplates().size() + " Templates :)");
        CloudAPI.getInstance().getCommandPool().registerCommand(new commsamdiawd());

    }

    @Override
    public void onShutdown() {

    }
}
