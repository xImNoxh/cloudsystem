package de.polocloud.api.config;

import de.polocloud.api.PoloCloudAPI;

import java.io.File;

/**
 * This interface has to implemented into a config-object
 * That wants to be saved and loaded
 * <br>
 * Example Config-Object:
 <hr><blockquote><pre>
 * public class ExampleConfig implements IConfig {
 *
 *   private final String host;
 *   private final int port;
 *   private final boolean testBoolean;
 *
 *   public ExampleConfig(String host, int port, boolean testBoolean) {
 *       this.host = host;
 *       this.port = port;
 *       this.testBoolean = testBoolean;
 *   }
 * }
 * </pre></blockquote><hr>
 */
public interface IConfig {

    /**
     * Default method to save this {@link IConfig} instance to a given {@link File}
     * through the {@link de.polocloud.api.config.saver.IConfigSaver} instance
     * at {@link PoloCloudAPI#getConfigSaver()}
     *
     * @param file the file to save it in
     */
    default void save(File file) {
        PoloCloudAPI.getInstance().getConfigSaver().save(this, file);
    }
}
