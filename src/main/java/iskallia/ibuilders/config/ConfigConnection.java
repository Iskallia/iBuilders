package iskallia.ibuilders.config;

import com.google.gson.annotations.Expose;
import iskallia.ibuilders.net.NetAddress;

import java.util.ArrayList;
import java.util.List;

public class ConfigConnection extends Config {

    @Expose public boolean IS_PLOT_SERVER;
    @Expose public int HOST_PORT = 0;
    @Expose public List<NetAddress> PLOT_SERVERS = new ArrayList<>();

    @Override
    public String getLocation() {
        return "connection.json";
    }

    @Override
    protected void resetConfig() {
        /*
        * The FunCraft live server would have a config like such:
        * IS_PLOT_SERVER = false;
        * PLOT_SERVERS.add(new NetAddress("PLOT_SERVER_IP", 5000)); //IP and "HOST_PORT" of the plot server.
        * */

        /*
        * The Plot server would have a config like such:
        * IS_PLOT_SERVER = true;
        * HOST_PORT = 5000; //On what port will the plot server expect connections from the live server(s)?
        * */
    }

}
