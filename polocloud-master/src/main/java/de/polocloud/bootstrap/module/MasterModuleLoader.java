package de.polocloud.bootstrap.module;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class MasterModuleLoader {

    public List<ModuleData> findModuleData(File directory) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("directory has to be a directory");
        }

        List<ModuleData> moduleData = new ArrayList<>();

        File[] files = directory.listFiles();

        for (File file : files) {
            if (file.isFile() && file.exists() && file.getName().endsWith(".jar")) {

                try (
                    JarFile jarFile = new JarFile(file);
                ) {

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        return moduleData;
    }

}
