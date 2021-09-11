package de.polocloud.modules.permission.global.api;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public interface Expireable {

    /**
     * Checks if this value has expired or if It's still valid
     *
     * @param expirationDate the date when it should expire
     * @return boolean
     */
    default boolean isStillValid(UUID uniqueId, long expirationDate) {
        if (expirationDate == -1) {
            return true;
        }
        Date today = new Date();
        Date endDate = new Date(expirationDate);
        long diffInMillis = endDate.getTime() - today.getTime();
        long d =  TimeUnit.MILLISECONDS.convert(diffInMillis,TimeUnit.MILLISECONDS);
        return (d >= 1);
    }
}
