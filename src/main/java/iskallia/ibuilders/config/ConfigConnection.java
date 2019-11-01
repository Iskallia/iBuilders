package iskallia.ibuilders.config;

import iskallia.ibuilders.net.NetAddress;

import java.util.ArrayList;
import java.util.List;

public class ConfigConnection extends Config {

    public List<NetAddress> MAIN_SERVERS = new ArrayList<>();
    public List<NetAddress> PLOT_SERVERS = new ArrayList<>();

    @Override
    public String getLocation() {
        return "connection.json";
    }

    @Override
    protected void resetConfig() {

    }

}
