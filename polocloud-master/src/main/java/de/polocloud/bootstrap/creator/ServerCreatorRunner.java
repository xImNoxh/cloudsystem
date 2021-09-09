package de.polocloud.bootstrap.creator;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.bootstrap.Master;

import java.util.Collection;

public class ServerCreatorRunner implements Runnable {


    /**
     * The creator instance
     */
    private final ServerCreator creator;

    public ServerCreatorRunner() {
        this.creator = new SimpleServerCreator();
    }

    @Override
    public void run() {

        while (Master.getInstance().isRunning()) {
            Collection<ITemplate> loadedTemplates = PoloCloudAPI.getInstance().getTemplateManager().getTemplates();
            loadedTemplates.stream().filter(creator::needsNewServer).forEach(creator::startServer);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
