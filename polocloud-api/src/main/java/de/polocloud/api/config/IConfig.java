package de.polocloud.api.config;

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

}
