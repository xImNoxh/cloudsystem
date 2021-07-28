package de.polocloud.plugin.api.template;

import de.polocloud.api.network.protocol.packet.api.template.APIRequestTemplatePacket;
import de.polocloud.api.network.response.ResponseHandler;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateLoader;
import de.polocloud.api.template.ITemplateSaver;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.protocol.NetworkClient;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class APITemplateManager implements ITemplateService {

    private final ExecutorService executor;
    private final NetworkClient networkClient;

    public APITemplateManager() {
        this.executor = Executors.newCachedThreadPool();
        networkClient = CloudPlugin.getInstance().getNetworkClient();
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
    public ITemplate getTemplateByName(String name) {
        return null;
    }

    @Override
    public CompletableFuture<Collection<ITemplate>> getLoadedTemplates() {
        CompletableFuture<Collection<ITemplate>> completableFuture = new CompletableFuture<>();
        executor.execute(() -> {
            UUID requestId = UUID.randomUUID();
            ResponseHandler.register(requestId, completableFuture);
            networkClient.sendPacket(new APIRequestTemplatePacket(requestId, APIRequestTemplatePacket.Action.ALL, "_"));
        });
        return completableFuture;
    }

    @Override
    public void reloadTemplates() {
        return;
    }

}
