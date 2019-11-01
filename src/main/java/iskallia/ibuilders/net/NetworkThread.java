package iskallia.ibuilders.net;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.init.InitConfig;
import iskallia.ibuilders.net.connection.Listener;
import iskallia.ibuilders.net.connection.ServerListener;

import java.util.HashSet;
import java.util.Set;

public class NetworkThread extends Thread {

    private ServerListener serverListener = null;
    private Set<Listener> listeners = new HashSet<>();

    private int sleepCounter = 0;

    public NetworkThread() {

    }

    @Override
    public void run() {
        //If the main a server plans to connect, start the server listener.
        if(InitConfig.CONFIG_CONNECTION.IS_PLOT_SERVER) {
            this.serverListener = new ServerListener(InitConfig.CONFIG_CONNECTION.HOST_PORT);

            this.serverListener.onConnectionEstablished(listener -> {
                Builders.LOG.warn("Main server [" + listener.getConnectionAddress() + "] connected to this plot world.");
            });

            this.serverListener.start();

            Builders.LOG.warn("Listener started and is awaiting.");
        }

        while(true) {
            //If the connection is lost, remove the listener from the list.
            this.listeners.removeIf(listener -> !listener.isConnected());

            for(NetAddress address: InitConfig.CONFIG_CONNECTION.PLOT_SERVERS) {
                boolean connected = false;

                for(Listener listener: this.listeners) {
                    if(listener.getConnectionAddress().equals(address)) {
                        connected = true;
                        break;
                    }
                }

                //If the plot server can't be reached, try again every 20 seconds.
                if(!connected && this.sleepCounter % (20 * 20) == 0) {
                    Listener listener = new Listener(address.getIp(), address.getPort());
                    listener.start();

                    Builders.LOG.warn("Attempting to connect to plot server [" + listener.getConnectionAddress() + "]...");

                    listener.onConnectionEstablished(connectedListener -> {
                        Builders.LOG.warn("Successfully connected to plot server [" + listener.getConnectionAddress() + "]!");
                    });

                    this.listeners.add(listener);
                }
            }

            this.sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(50);
            this.sleepCounter++;
        } catch(InterruptedException e) {e.printStackTrace();}
    }

}
