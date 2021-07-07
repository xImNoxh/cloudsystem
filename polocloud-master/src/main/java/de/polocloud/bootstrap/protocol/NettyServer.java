package de.polocloud.bootstrap.protocol;

import com.esotericsoftware.kryonetty.ThreadedServerEndpoint;
import com.esotericsoftware.kryonetty.kryo.KryoNetty;

public class NettyServer {

    private ThreadedServerEndpoint threadedServerEndpoint;
    private KryoNetty kryoNetty;

    public NettyServer() {
        this.kryoNetty = new KryoNetty();
        this.threadedServerEndpoint = new ThreadedServerEndpoint(kryoNetty);

        initMasterService();
    }

    public void initMasterService(){
        this.threadedServerEndpoint.start(8000);
    }

    public ThreadedServerEndpoint getThreadedServerEndpoint() {
        return threadedServerEndpoint;
    }
}
