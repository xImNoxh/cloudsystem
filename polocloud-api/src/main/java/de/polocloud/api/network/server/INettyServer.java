package de.polocloud.api.network.server;

import de.polocloud.api.network.IStartable;
import de.polocloud.api.network.ITerminatable;
import de.polocloud.api.network.protocol.IProtocol;

public interface INettyServer extends IStartable, ITerminatable {

    IProtocol getProtocol();

}
