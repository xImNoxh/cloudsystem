package de.polocloud.api.network.request;


import de.polocloud.api.network.request.base.future.PoloFuture;
import de.polocloud.api.network.request.base.other.IRequestHandler;

public interface IRequestManager {

    /**
     * Registers an {@link IRequestHandler}
     *
     * @param handler the handler
     */
    void registerRequestHandler(IRequestHandler<?> handler);

    /**
     * Unregisters an {@link IRequestHandler}
     *
     * @param handler the handler
     */
    void unregisterRequestHandler(IRequestHandler<?> handler);

    /**
     * Adds a {@link PoloFuture} to the cache with a given id
     *
     * @param id the id of the request
     * @param future the future
     */
    void addRequest(String id, PoloFuture<?> future);

    /**
     * Gets an {@link PoloFuture} from cache
     * and then automatically removes it from cache
     *
     * @param id the id
     * @return future or null
     */
    PoloFuture<?> retrieveFuture(String id);


}
