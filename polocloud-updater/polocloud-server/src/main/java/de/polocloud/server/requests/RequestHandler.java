package de.polocloud.server.requests;

import de.polocloud.server.requests.exception.ExceptionRequestHandler;
import de.polocloud.server.requests.update.ChangelogRequestHandler;
import de.polocloud.server.requests.update.StatusRequestHandler;
import de.polocloud.server.requests.update.UpdateRequestHandler;

public class RequestHandler {

    private ExceptionRequestHandler exceptionRequestHandler;
    private StatusRequestHandler statusRequestHandler;
    private UpdateRequestHandler updateRequestHandler;
    private ChangelogRequestHandler changelogRequestHandler;

    public RequestHandler() {
        this.exceptionRequestHandler = new ExceptionRequestHandler();
        this.statusRequestHandler = new StatusRequestHandler();
        this.updateRequestHandler = new UpdateRequestHandler();
        this.changelogRequestHandler = new ChangelogRequestHandler();
    }

    public ExceptionRequestHandler getExceptionRequestHandler() {
        return exceptionRequestHandler;
    }

    public StatusRequestHandler getStatusRequestHandler() {
        return statusRequestHandler;
    }

    public UpdateRequestHandler getUpdateRequestHandler() {
        return updateRequestHandler;
    }

    public ChangelogRequestHandler getChangelogRequestHandler() {
        return changelogRequestHandler;
    }
}
