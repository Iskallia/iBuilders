package iskallia.ibuilders.net.connection;

import iskallia.ibuilders.net.packet.Packet;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerListener extends Thread {

    private boolean shouldDisconnect;

    private ServerSocket serverSocket;
    private Map<Integer, Listener> listeners = new HashMap<>();

    public ServerListener(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch(IOException e) {
            e.printStackTrace();
            this.disconnect();
        }
    }

    private void listen() {
        try {
            System.out.println("Server Started.");

            while(this.isConnected() && !serverSocket.isClosed()) {
                Listener listener = new Listener(serverSocket.accept());
                listener.start();

                if(listener.isConnected()) {
                    this.listeners.put(listener.getListenerId(), listener);
                }

                this.listeners = this.listeners.entrySet().stream()
                        .filter(entry -> entry.getValue().isConnected())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            }
        } catch(IOException e)  {
            e.printStackTrace();
        }

        this.disconnect();
    }

    public boolean isConnected() {
        return !this.shouldDisconnect;
    }


    public void disconnect() {
        if(!this.isConnected())return;

        this.shouldDisconnect = true;

        try {this.serverSocket.close();}
        catch(Exception e) {;}
    }

    public List<Listener> getListeners() {
        return this.listeners.values().stream()
                .filter(Listener::isConnected)
                .sorted(Comparator.comparingInt(Listener::getListenerId))
                .collect(Collectors.toList());
    }

    public Listener getFromListenerId(int listenerId) {
        return this.listeners.get(listenerId);
    }

    public void sendPacketTo(int listenerId, Packet packet) {
        this.getFromListenerId(listenerId).sendPacket(packet);
    }

    public void sendPacketToAll(Packet packet) {
        this.listeners.values().forEach(l -> l.sendPacket(packet));
    }

    public void sendPacketToAllExcept(int listenerId, Packet packet) {
        this.listeners.values().forEach(l -> {
            if(l.getListenerId() != listenerId) {
                l.sendPacket(packet);
            }
        });
    }

    @Override
    public void run() {
        this.listen();
    }

}
