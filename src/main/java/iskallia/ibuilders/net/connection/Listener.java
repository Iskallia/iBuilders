package iskallia.ibuilders.net.connection;

import iskallia.ibuilders.net.NetAddress;
import iskallia.ibuilders.net.context.ClientContext;
import iskallia.ibuilders.net.context.Context;
import iskallia.ibuilders.net.context.ServerContext;
import iskallia.ibuilders.net.packet.Packet;
import iskallia.ibuilders.net.packet.PacketHandler;
import it.unimi.dsi.fastutil.bytes.ByteCollections;
import net.minecraftforge.fml.relauncher.Side;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Listener extends Thread {

    private static final PacketHandler PACKET_HANDLER = new PacketHandler();
    private static int ID = 0;

    private Socket socket;
    private NetAddress connectionAddress;
    private int listenerId;

    private DataInputStream socketInputStream;
    private DataOutputStream socketOutputStream;
    private boolean shouldDisconnect;

    private Side side = Side.CLIENT;
    private ServerListener serverListener;

    private List<Consumer<Listener>> connectionEstablishedConsumers = new ArrayList<>();

    public Listener(Socket socket) {
        this.connectionAddress = new NetAddress(socket.getInetAddress().getHostAddress(), socket.getPort());

        try {
            this.socket = socket;
            this.socketInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this.socketOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch(IOException e) {
            e.printStackTrace();
            this.disconnect();
            return;
        }

        this.listenerId = ID++;
    }

    public Listener(String address, int port) {
        this.connectionAddress = new NetAddress(address, port);

        try {
            this.socket = new Socket(address, port);
            this.socketInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this.socketOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch(IOException e) {
            e.printStackTrace();
            this.disconnect();
            return;
        }

        this.listenerId = ID++;
    }

    public int getListenerId() {
        return this.listenerId;
    }

    public NetAddress getConnectionAddress() {
        return this.connectionAddress;
    }

    public void setServer(ServerListener serverListener) {
        this.side = Side.SERVER;
        this.serverListener = serverListener;
    }

    private Context getContext() {
        if(this.side == Side.CLIENT) {
            ClientContext clientContext = new ClientContext();
            return clientContext;
        } else if(this.side == Side.SERVER) {
            ServerContext serverContext = new ServerContext();
            serverContext.serverListener = this.serverListener;
            serverContext.listener = this;
            return serverContext;
        }

        return null;
    }

    public void onConnectionEstablished(Consumer<Listener> listenerConsumer) {
        this.connectionEstablishedConsumers.add(listenerConsumer);
    }

    private void listen() {
        if(!this.isConnected())return;

        for(Consumer<Listener> listenerConsumer: this.connectionEstablishedConsumers) {
            listenerConsumer.accept(this);
        }

        while(this.isConnected() && !this.socket.isClosed()) {
            String data = this.readPacket();
            if(data == null)continue;

            PACKET_HANDLER.onPacketReceived(data, this.getContext());
        }

        this.disconnect();
    }

    private String readPacket() {
        try {return this.socketInputStream.readUTF();}
        catch(IOException e) {this.disconnect();}
        return null;
    }

    public boolean isConnected() {
        return !this.shouldDisconnect;
    }

    public synchronized void disconnect() {
        if(!this.isConnected())return;

        this.shouldDisconnect = true;

        try {this.socketInputStream.close();}
        catch(Exception e) {;}

        try {this.socketOutputStream.close();}
        catch(Exception e) {;}

        try {this.socket.close();}
        catch(Exception e) {;}
    }

    public void sendPacket(Packet packet) {
        if(this.shouldDisconnect)return;
        if(!packet.canSendFrom(this.side))return;

        String data = PACKET_HANDLER.packPacket(packet);
        try {this.socketOutputStream.writeUTF(data);}
        catch(IOException e) {this.disconnect();}
    }

    @Override
    public void run() {
        this.listen();
    }

}
