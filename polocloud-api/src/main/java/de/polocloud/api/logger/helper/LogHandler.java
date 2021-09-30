package de.polocloud.api.logger.helper;

import de.polocloud.api.logger.PoloLog;
import de.polocloud.api.util.other.Cancellable;

public interface LogHandler {

    /**
     * Called when handling a log message
     *
     * @param cancellable the cancellable to cancel default printing of line
     * @param log the log instance
     * @param message the message to log
     */
    void handleLoggedMessage(Cancellable cancellable, PoloLog log, String message);
}
