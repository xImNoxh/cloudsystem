package de.polocloud.signs.service;

/**
 * General interface for the {@link de.polocloud.signs.plugin.SignsPluginService}
 * and the {@link de.polocloud.signs.module.ModuleSignService}
 */
public interface ISignService {

    void loadSigns();

    void reloadSigns();

    void updateSigns();

    void registerListeners();

}
