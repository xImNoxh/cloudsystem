package de.polocloud.server.threaded;

import de.polocloud.server.threaded.exception.ExceptionWriter;
import de.polocloud.server.threaded.refresh.RefreshThread;

public class ThreadProvider {

    private ExceptionWriter exceptionWriter;
    private RefreshThread refreshThread;

    public ThreadProvider() {
        exceptionWriter = new ExceptionWriter();
        refreshThread = new RefreshThread();
    }

    public ExceptionWriter getExceptionWriter() {
        return exceptionWriter;
    }

    public RefreshThread getRefreshThread() {
        return refreshThread;
    }
}
