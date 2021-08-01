package de.polocloud.bootstrap.module;

public class ModuleLocalCache {

    private ModuleClassLoader loader;
    private ModuleData moduleData;

    public ModuleLocalCache(ModuleClassLoader loader, ModuleData moduleData) {
        this.loader = loader;
        this.moduleData = moduleData;
    }

    public ModuleClassLoader getLoader() {
        return loader;
    }

    public ModuleData getModuleData() {
        return moduleData;
    }
}
