package de.polocloud.party.bootstrap;

import de.polocloud.api.module.Module;
import de.polocloud.party.PartyService;

public class PartyBootstrap extends Module {

    private static PartyBootstrap instance;

    @Override
    public void onLoad() {
        new PartyService();
    }

    @Override
    public void onShutdown() {

    }

    public static PartyBootstrap getInstance() {
        return instance;
    }
}
