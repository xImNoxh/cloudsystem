package de.polocloud.plugin.api.template;

import de.polocloud.api.network.protocol.packet.api.template.APIRequestTemplatePacket;
import de.polocloud.api.network.protocol.packet.api.template.APIResponseTemplatePacket;
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

    public void sendRequest(CompletableFuture<?> type, APIRequestTemplatePacket.Action action, String value){
        UUID uuid = UUID.randomUUID();
        ResponseHandler.register(uuid, type);
        networkClient.sendPacket(new APIRequestTemplatePacket(uuid, action, value));
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
        CompletableFuture<ITemplate> completableFuture = new CompletableFuture<>();
        executor.execute(() -> sendRequest(completableFuture, APIRequestTemplatePacket.Action.NAME, name));
        return completableFuture;
    }

    @Override
    public CompletableFuture<Collection<ITemplate>> getLoadedTemplates() {
        CompletableFuture<Collection<ITemplate>> completableFuture = new CompletableFuture<>();
        executor.execute(() -> sendRequest(completableFuture, APIRequestTemplatePacket.Action.ALL, "_"));
        return completableFuture;
    }

    @Override
    public void reloadTemplates() {
        return;
    }

}
