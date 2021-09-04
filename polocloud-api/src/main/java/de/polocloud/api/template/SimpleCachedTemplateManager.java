package de.polocloud.api.template;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.packets.gameserver.GameServerCopyPacket;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.loading.ITemplateLoader;
import de.polocloud.api.template.loading.ITemplateSaver;
import de.polocloud.api.wrapper.base.IWrapper;

import java.util.ArrayList;
import java.util.List;

public class SimpleCachedTemplateManager implements ITemplateManager {

    private List<ITemplate> templates;
    private ITemplateSaver templateSaver;
    private ITemplateLoader templateLoader;


    public SimpleCachedTemplateManager() {
        this.templates = new ArrayList<>();
    }

    @Override
    public void loadTemplates(TemplateStorage storage) {
        this.templateLoader = PoloCloudAPI.getInstance().getGuice().getInstance(storage.getTemplateLoader());
        this.templateSaver = PoloCloudAPI.getInstance().getGuice().getInstance(storage.getTemplateServer());
        this.reloadTemplates();
    }

    @Override
    public void copyServer(IGameServer gameServer, Type type) {
        for (IWrapper wrapper : gameServer.getAllWrappers()) {
            wrapper.sendPacket(new GameServerCopyPacket(type, gameServer));
        }
    }

    @Override
    public void setCachedObjects(List<ITemplate> templates) {
        this.templates = templates;
    }

    @Override
    public ITemplate getTemplate(String name) {
        return this.templates.stream().filter(temp -> temp.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public void reloadTemplates() {
        this.templates = this.templateLoader.loadTemplates();
    }

    @Override
    public List<ITemplate> getTemplates() {
        return templates;
    }

    @Override
    public ITemplateLoader getTemplateLoader() {
        return templateLoader;
    }

    @Override
    public ITemplateSaver getTemplateSaver() {
        return templateSaver;
    }
}
