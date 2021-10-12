package de.polocloud.api.util.session;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.protocol.IProtocolObject;

import java.util.UUID;

public interface ISession extends IProtocolObject {

    /**
     * Generates a new random {@link ISession}
     *
     * @return the created session instance
     */
    static ISession randomSession() {
        ISession session = PoloCloudAPI.getInstance().getInjector().getInstance(ISession.class);
        session.randomize();
        return session;
    }

    /**
     * Randomizes this session
     * with random values
     */
    void randomize();

    /**
     * The bytes of this {@link ISession}
     */
    byte[] getBytes();

    /**
     * The snowflake of this {@link ISession}
     */
    long getSnowflake();

    /**
     * The session identifier of this {@link ISession} as {@link UUID}
     */
    UUID getUniqueId();

    /**
     * The identification String to separate {@link ISession}s
     */
    String getIdentification();

    /**
     * Checks if this {@link ISession} equals another {@link ISession}
     *
     * @param other the other id to check
     * @return boolean if equals
     */
    boolean equals(ISession other);

    /**
     * Basis information in this string
     */
    String toString();
}
