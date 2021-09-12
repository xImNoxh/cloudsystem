package de.polocloud.api.config.loader;

import de.polocloud.api.config.IConfig;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.util.gson.PoloHelper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SimpleConfigLoader implements IConfigLoader {

    @Override
    public <T> T load(Class<? extends IConfig> configClass, File file) {
        if (!file.exists()) {
            return (T) PoloHelper.getInstance(configClass);
        }
        //T result = null;
       // try {
//
           // FileReader reader = new FileReader(file);
           // result = (T) PoloHelper.GSON_INSTANCE.fromJson(reader, configClass);
         //   reader.close();
       // } catch (IOException e) {
        //    e.printStackTrace();
        //}
        return (T) new JsonData(file).getAs(configClass);
    }

    @Override
    public <T> T load(Class<? extends IConfig> configClass, IConfig defaultConfig, File file, IConfigSaver saver) {
        if (!file.exists()) {
            saver.save(defaultConfig, file);
            return (T) defaultConfig;
        }
        return (T) new JsonData(file).getAs(configClass);
        //try {
            //result = (T) new JsonData(file).getAs(configClass);
            //FileReader reader = new FileReader(file);
            //result = (T) PoloHelper.GSON_INSTANCE.fromJson(reader, configClass);
            //reader.close();
        //} catch (IOException e) {
            //e.printStackTrace();
        //}
        //return result;
    }
}
