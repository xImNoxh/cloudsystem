package de.polocloud.wrapper.manager.screen.impl;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.wrapper.manager.screen.IScreen;
import de.polocloud.wrapper.manager.screen.IScreenManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class SimpleCachedScreenManager implements IScreenManager {

    /**
     * The cached {@link IScreen}s
     */
    private final Map<String, IScreen> registeredScreens;

    /**
     * The current screen
     */
    private IScreen screen;

    /**
     * If currently in screen
     */
    private boolean inScreen;

    public SimpleCachedScreenManager() {
        this.registeredScreens = new HashMap<>();
    }

    /**
     * Sets current screen
     *
     * @param screen the screen
     */
    public void prepare(IScreen screen) {
        this.screen = screen;
        this.inScreen = true;
    }

    /**
     * Leaves current screen
     */
    public void quitCurrentScreen() {
        this.inScreen = false;
        PoloCloudAPI.getInstance().getCommandManager().setFilter(null);
        if (this.screen == null) {
            return;
        }

        PoloLogger.print(LogLevel.INFO, "§cYou left the §esession §cof the service §e" + this.screen.getScreenName() + "§c!");
        if (this.screen.getThread().isAlive()) {
            try {
                this.screen.stop();
            } catch (ThreadDeath e) {
                //Ignoring
            }
        }
        this.screen = null;
    }


    @Override
    public IScreen getScreen() {
        return screen;
    }

    @Override
    public void unregisterScreen(String name) {
        this.registeredScreens.remove(name);
    }

    @Override
    public void registerScreen(String name, IScreen screen) {
        registeredScreens.put(name.toLowerCase(), screen);
    }

    @Override
    public List<IScreen> getScreens() {
        return new LinkedList<>(this.registeredScreens.values());
    }

    @Override
    public boolean isInScreen() {
        return inScreen;
    }

    /**
     * Gets a {@link IScreen} if its on this instance
     * by just getting it from the hashmap cache
     *
     * @param name the name
     * @return screen output
     */
    public IScreen getScreen(String name) {
        return this.registeredScreens.get(name.toLowerCase());
    }

}
