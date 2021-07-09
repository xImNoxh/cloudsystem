package de.polocloud.bootstrap.creator;

import com.google.inject.Inject;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.bootstrap.Master;

import java.util.Collection;

public class ServerCreatorRunner implements Runnable {

    @Inject
    private Master master;

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private ITemplateService templateService;

    private ServerCreator creator = CloudAPI.getInstance().getGuice().getInstance(SimpleServerCreator.class);

    @Override
    public void run() {

        while (master.isRunning()) {

            Collection<ITemplate> loadedTemplates = templateService.getLoadedTemplates();

            for (ITemplate loadedTemplate : loadedTemplates) {
                if (creator.check(loadedTemplate)) {
                    creator.startServer(loadedTemplate);
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
