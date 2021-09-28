package de.polocloud.api.template;

import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.packets.gameserver.GameServerCopyPacket;
import de.polocloud.api.network.packets.gameserver.GameServerExecuteCommandPacket;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.helper.TemplateType;
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
        this.templateLoader = storage.getTemplateLoader();
        this.templateSaver =  storage.getTemplateServer();
        this.reloadTemplates();
    }

    @Override
    public void copyServer(IGameServer gameServer, Type type) {
        for (IWrapper wrapper : gameServer.getWrappers()) {
            if(gameServer.getTemplate().getTemplateType().equals(TemplateType.MINECRAFT)){
                gameServer.sendPacket(new GameServerExecuteCommandPacket("save-all", gameServer.getName()));
            }
            wrapper.sendPacket(new GameServerCopyPacket(type, gameServer));
        }
    }

    public void update(ITemplate template) {
        if (this.templates.stream().anyMatch(t -> t.getName().equalsIgnoreCase(template.getName()))) {
            this.templates.removeIf(t -> t.getName().equalsIgnoreCase(template.getName()));
            this.templates.add(template);
        }
    }

    @Override
    public void addTemplate(ITemplate template) {
        this.templates.add(template);
        this.templateSaver.save(template);
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
