package de.polocloud.plugin.api.template;

import de.polocloud.api.network.protocol.packet.api.template.APIRequestTemplatePacket;
import de.polocloud.api.network.response.ResponseHandler;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateLoader;
import de.polocloud.api.template.ITemplateSaver;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.plugin.CloudPlugin;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class APITemplateManager implements ITemplateService {

    private final ExecutorService executor;

    public APITemplateManager() {
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public ITemplateLoader getTemplateLoader() {
        return null;
    }

    @Override
    public ITemplateSaver getTemplateSaver() {
        return null;
    }

    @Override
    public CompletableFuture<ITemplate> getTemplateByName(String name) {
        return (CompletableFuture<ITemplate>) sendRequest( new CompletableFuture<ITemplate>(), APIRequestTemplatePacket.Action.NAME, name);
    }

    @Override
    public CompletableFuture<Collection<ITemplate>> getLoadedTemplates() {
        return (CompletableFuture<Collection<ITemplate>>) sendRequest(new CompletableFuture<Collection<ITemplate>>(), APIRequestTemplatePacket.Action.ALL, "_");

    }

    @Override
    public void reloadTemplates() {
        return;
    }

    public CompletableFuture<?> sendRequest(CompletableFuture<?> future, APIRequestTemplatePacket.Action action, String value){
        executor.execute(() -> {
            UUID uuid = UUID.randomUUID();
            ResponseHandler.register(uuid, future);
            CloudPlugin.getCloudPluginInstance().getNetworkClient().sendPacket(new APIRequestTemplatePacket(uuid, action, value));
        });
        return future;
    }

}
