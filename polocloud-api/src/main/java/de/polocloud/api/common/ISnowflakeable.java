package de.polocloud.api.common;

public interface ISnowflakeable {

    /**
     * Returns a snowflake long to identify
     * this object in contrast to others
     */
    long getSnowflake();
}
