package de.polocloud.api.network.request.base.other;

import de.polocloud.api.network.request.base.component.PoloComponent;

public interface IRequestHandler<T> {

    /**
     * Handles this request
     *
     * @param request the request
     */
    void handle(PoloComponent<T> request);
}
