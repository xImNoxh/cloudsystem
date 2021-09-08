package de.polocloud.signs.sign.base;

import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.signs.sign.enumeration.SignState;
import de.polocloud.signs.sign.location.SignLocation;
import org.bukkit.Location;
import org.bukkit.block.Sign;

public interface IGameServerSign {


    /**
     * Main-method for writing the sign
     * @param writeClean if true, the sign will reset its lines
     *                   before writing
     */
    void writeSign(boolean writeClean);

    /**
     * Updates the block behind the Sign
     * and rewrites the sign new and clean
     */
    void cleanUp();

    /**
     * Searches for a new {@link SignState} for the sign
     */
    void updateSignState();

    /**
     * Resets the lastInput of the Sign and rewrites it
     */
    void updateSign();

    /**
     * Sets the Bukkit-{@link Sign} of the Sign and {@link #writeSign(boolean) writes}
     * the sign new and clean
     * @param sign the new {@link Sign} of the Sign
     */
    void reloadSign(Sign sign);

    /**
     *  Returns the {@link IGameServer} of the Sign
     */
    IGameServer getGameServer();

    /**
     *  Returns the {@link ITemplate} of the Sign
     */
    ITemplate getTemplate();

    /**
     *  Returns the {@link SignState} of the Sign
     */
    SignState getSignState();

    /**
     *  Returns the Bukkit-{@link Sign} of the Sign
     */
    Sign getSign();

    /**
     *  Returns the Module-{@link SignLocation} of the Sign
     */
    SignLocation getSignLocation();

    /**
     *  Returns the Bukkit-{@link Location} of the Sign
     */
    Location getLocation();

    /**
     * Sets the {@link IGameServer} of the Sign
     * @param gameServer the new template for the Sign
     */
    void setGameServer(IGameServer gameServer);

    /**
     * Sets the {@link ITemplate} of the Sign
     * @param template the new template for the Sign
     */
    void setTemplate(ITemplate template);

    /**
     * Sets the Module-{@link SignLocation} of the Sign
     * @param location the new location for the Sign
     */
    void setSignLocation(SignLocation location);

    /**
     * Sets the {@link SignState} of the sign
     * @param signState the new {@link SignState} for the sign
     */
    void setSignState(SignState signState);

    /**
     * Sets the Bukkit-{@link Sign} of the Sign
     * @param sign the new sign for the sign
     */
    void setSign(Sign sign);

    /**
     * Sets the Bukkit-{@link Location} of the Sign
     * @param location the new location for the Sign
     */
    void setLocation(Location location);

}
