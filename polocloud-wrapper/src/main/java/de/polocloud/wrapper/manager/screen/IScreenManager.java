package de.polocloud.wrapper.manager.screen;

import java.util.List;

public interface IScreenManager {

    /**
     * Gets a list of all running {@link IScreen}s
     *
     * @return list of screen
     */
    List<IScreen> getScreens();

    /**
     * Checks if in screen currently
     *
     * @return boolean
     */
    boolean isInScreen();

    /**
     * Leaves the current screen
     */
    void quitCurrentScreen();

    /**
     * Gets the current {@link IScreen}
     *
     * @return screen or null if not in any screen
     */
    IScreen getScreen();

    /**
     * Tries to get a {@link IScreen} from cache
     * If it's not cached it's not on this instance
     * and then a request will be sent and awaits for response
     * from the instance where the screen is running on
     *
     * @param name the name of the service
     * @return screen or null if timed out or not found
     */
    IScreen getScreen(String name);

    /**
     * Prepares an {@link IScreen}
     *
     * @param screen the screen
     */
    void prepare(IScreen screen);

    /**
     * Registers an {@link IScreen}
     *
     * @param name the name
     * @param screen the screen
     */
    void registerScreen(String name, IScreen screen);

    /**
     * Unregisters an {@link IScreen} from cache
     *
     * @param name the name of the screen
     */
    void unregisterScreen(String name);
}
