package iskallia.ibuilders.init;

import iskallia.ibuilders.config.ConfigConnection;

public class InitConfig {

    public static ConfigConnection CONFIG_CONNECTION = null;

    public static void registerConfigs() {
        CONFIG_CONNECTION = (ConfigConnection)new ConfigConnection().readConfig();
    }

}
