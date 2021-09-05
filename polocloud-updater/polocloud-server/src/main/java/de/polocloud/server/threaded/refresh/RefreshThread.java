package de.polocloud.server.threaded.refresh;

import de.polocloud.server.PoloCloudServer;

import java.text.SimpleDateFormat;

public class RefreshThread extends Thread {

    private boolean running = false;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public RefreshThread() {
        start();
    }

    @Override
    public void run() {
        running = true;
        try {
            while (running) {
                sleep(30000);
                System.out.println("[" + simpleDateFormat.format(System.currentTimeMillis()) + "] Refreshing config...");
                PoloCloudServer.getInstance().loadConfig();
            }
        } catch (Exception exception) {
            running = false;
            exception.printStackTrace();
        }
    }

    public void destroyRefreshThread() {
        running = false;
    }

}
