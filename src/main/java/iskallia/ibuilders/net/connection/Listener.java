package iskallia.ibuilders.net.connection;

import iskallia.ibuilders.net.context.ClientContext;
import iskallia.ibuilders.net.context.Context;
import iskallia.ibuilders.net.context.ServerContext;
import iskallia.ibuilders.net.packet.Packet;
import iskallia.ibuilders.net.packet.PacketHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Listener extends Thread {

    private static final PacketHandler PACKET_HANDLER = new PacketHandler();
    private static int ID = 0;

    private Socket socket;
    private int listenerId;

    private DataInputStream socketInputStream;
    private DataOutputStream socketOutputStream;
    private boolean shouldDisconnect;

    private Side side;

    public Listener(Socket socket) {
        System.out.println("Connecting to " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + ".");

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
        System.out.println("Connecting to " + address + ":" + port + ".");

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

    private Context getContext() {
        if(this.side == Side.CLIENT) {
            ClientContext clientContext = new ClientContext();
            return clientContext;
        } else if(this.side == Side.SERVER) {
            ServerContext serverContext = new ServerContext();
            //TODO: Get the server listener instance.
            //serverContext.serverListener = ;
            serverContext.listener = this;
            return serverContext;
        }

        return null;
    }

    private void listen() {
        if(!this.isConnected())return;
        System.out.println("Connection established.");

        while(this.isConnected() && !this.socket.isClosed()) {
            String data = this.readPacket();
            if(data == null)continue;

            PACKET_HANDLER.onPacketRecieved(data, this.getContext());
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

    public void disconnect() {
        if(!this.isConnected())return;

        this.shouldDisconnect = true;

        try {this.socketInputStream.close();}
        catch(Exception e) {;}

        try {this.socketOutputStream.close();}
        catch(Exception e) {;}

        try {this.socket.close();}
        catch(Exception e) {;}

        System.out.println("Disconnected.");
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
