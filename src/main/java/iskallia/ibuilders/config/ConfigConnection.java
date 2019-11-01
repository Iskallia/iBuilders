package iskallia.ibuilders.config;

import com.google.gson.annotations.Expose;
import iskallia.ibuilders.net.NetAddress;

import java.util.ArrayList;
import java.util.List;

public class ConfigConnection extends Config {

    @Expose public int HOST_PORT = 0;
    @Expose public List<NetAddress> MAIN_SERVERS = new ArrayList<>();
    @Expose public List<NetAddress> PLOT_SERVERS = new ArrayList<>();

    @Override
    public String getLocation() {
        return "connection.json";
    }

    @Override
    protected void resetConfig() {

    }

}
