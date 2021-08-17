package de.polocloud.bootstrap.creator;

import com.google.inject.Inject;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.Master;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class ServerCreatorRunner implements Runnable {

    @Inject
    private Master master;

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private ITemplateService templateService;

    private final ServerCreator creator = PoloCloudAPI.getInstance().getGuice().getInstance(SimpleServerCreator.class);

    @Override
    public void run() {

        while (master.isRunning()) {
            try {
                Collection<ITemplate> loadedTemplates = templateService.getLoadedTemplates().get();
                loadedTemplates.stream().filter(creator::check).forEach(creator::startServer);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
