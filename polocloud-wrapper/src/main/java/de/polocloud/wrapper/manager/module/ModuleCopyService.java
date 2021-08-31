package de.polocloud.wrapper.manager.module;

import de.polocloud.api.module.ModuleCopyType;

import java.io.File;
import java.util.LinkedHashMap;

public class ModuleCopyService {

    private LinkedHashMap<File, ModuleCopyType[]> cachedModule;

    public ModuleCopyService() {
        cachedModule = new LinkedHashMap<>();
    }

    public LinkedHashMap<File, ModuleCopyType[]> getCachedModule() {
        return cachedModule;
    }

    public void setCachedModule(LinkedHashMap<File, ModuleCopyType[]> cachedModule) {
        this.cachedModule = cachedModule;
    }

    public void addCachedModule(File f, ModuleCopyType[] info) {
        this.cachedModule.put(f, info);
    }
}
