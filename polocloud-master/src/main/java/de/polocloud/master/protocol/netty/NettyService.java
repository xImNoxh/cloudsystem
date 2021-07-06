package de.polocloud.master.protocol.netty;

import com.esotericsoftware.kryonetty.ThreadedServerEndpoint;
import com.esotericsoftware.kryonetty.kryo.KryoNetty;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

public class NettyService {

    private KryoNetty netty;
    private ThreadedServerEndpoint server;

    public NettyService() {

        Logger.log(LoggerType.INFO, ConsoleColors.GRAY.getAnsiCode() + "Trying to bind to port " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + 8001 + ConsoleColors.GRAY.getAnsiCode() + ".");

        netty = new KryoNetty().threadSize(16).inputSize(4096).outputSize(4096).maxOutputSize(-1);
        server = new ThreadedServerEndpoint(netty);
        server.start(8001);
    }

    public KryoNetty getNetty() {
        return netty;
    }

    public ThreadedServerEndpoint getServer() {
        return server;
    }
}
