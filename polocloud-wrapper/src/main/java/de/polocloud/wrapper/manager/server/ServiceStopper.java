package de.polocloud.wrapper.manager.server;

import de.polocloud.api.config.FileConstants;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.util.PoloHelper;
import de.polocloud.wrapper.Wrapper;
import de.polocloud.wrapper.manager.screen.IScreen;
import de.polocloud.wrapper.manager.screen.IScreenManager;

import java.io.File;
import java.util.function.Consumer;

public class ServiceStopper {

    private final IGameServer gameServer;

    public ServiceStopper(IGameServer gameServer) {
        this.gameServer = gameServer;
    }

    /**
     * Stops a given service
     *
     * @param consumer consumer to accept after finish
     */
    public void stop(Consumer<IGameServer> consumer) {

        IScreenManager screenManager = Wrapper.getInstance().getScreenManager();
        IScreen screen = screenManager.getScreen(gameServer.getName());

        if (screen == null) {
            PoloLogger.print(LogLevel.ERROR, "§cCan't stop §e" + gameServer.getName() + " §cbecause no Screen with Process was found!");
            return;
        }

        Process process = screen.getProcess();

        //thread.stop(); //Stopping thread
        process.destroy(); //Shutting down process

        Scheduler.runtimeScheduler().schedule(() -> {

            //It's dynamic delete whole directory
            if (gameServer.getTemplate().isDynamic()) {
                PoloHelper.deleteFolder(screen.getDirectory());
                consumer.accept(gameServer);
                return;
            }

            //Static ----> only remove Cloud-Folder and CloudBridge
            File bridgeFile = new File(screen.getDirectory(), "plugins/" + FileConstants.CLOUD_API_NAME);
            if (!bridgeFile.exists()) {
                consumer.accept(gameServer);
                return;
            }

            PoloHelper.deleteFolder(new File(screen.getDirectory(), "CLOUD"));
            if (bridgeFile.delete()) {
                consumer.accept(gameServer);
            }
            screenManager.unregisterScreen(gameServer.getName());
        }, 5L);
    }
}
