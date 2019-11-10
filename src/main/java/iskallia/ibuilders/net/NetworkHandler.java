package iskallia.ibuilders.net;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.init.InitConfig;
import iskallia.ibuilders.net.connection.Listener;
import iskallia.ibuilders.net.connection.ServerListener;
import iskallia.ibuilders.net.packet.Packet;
import iskallia.ibuilders.net.packet.PacketC2SHandshake;

import java.util.concurrent.ConcurrentLinkedDeque;

public class NetworkHandler {

    private boolean hasStarted = false;
    private long time;

    private ServerListener serverListener;
    private ConcurrentLinkedDeque<Listener> listeners = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<Runnable> runnables = new ConcurrentLinkedDeque<>();

    public void start() {
        //If this is a plot server, start the server listener.
        if(InitConfig.CONFIG_CONNECTION.IS_PLOT_SERVER) {
            this.startPlotServerListener();
        }

        this.hasStarted = true;
    }

    public void tick() {
        if(!this.hasStarted) {
            throw new IllegalStateException("NetworkHandler hasn't been initialized!");
        }

        if(this.time % (20 * 20) == 0) {
            this.connectToPlotServers();
        }

        this.runnables.removeIf(runnable -> {
           runnable.run();
           return true;
        });

        this.time++;
    }

    public boolean hasServerListener() {
        return this.serverListener != null;
    }

    public ServerListener getServerListener() {
        return this.serverListener;
    }

    public void sendToAllPlotServers(Packet packet) {
        if(this.listeners.isEmpty())return;

        this.runnables.add(() -> {
            this.listeners.removeIf(listener -> !listener.isConnected());

            this.listeners.forEach(listener -> {
                listener.sendPacket(packet);
            });
        });
    }

    private void startPlotServerListener() {
        this.serverListener = new ServerListener(InitConfig.CONFIG_CONNECTION.HOST_PORT);

        this.serverListener.onConnectionEstablished(listener -> {
            Builders.LOG.warn("Main server [" + listener.getConnectionAddress() + "] connected to this plot world.");
        });

        this.serverListener.start();

        Builders.LOG.warn("Listener started and is awaiting.");
    }

    private void connectToPlotServers() {
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

            //If the plot server can't be reached, try to connect again.
            if(!connected) {
                Listener listener = new Listener(address.getIp(), address.getPort());
                listener.start();

                Builders.LOG.warn("Attempting to connect to plot server [" + listener.getConnectionAddress() + "]...");

                listener.onConnectionEstablished(connectedListener -> {
                    Builders.LOG.warn("Successfully connected to plot server [" + connectedListener.getConnectionAddress() + "]!");
                    connectedListener.sendPacket(new PacketC2SHandshake());
                });

                this.listeners.add(listener);
            }
        }
    }

}
