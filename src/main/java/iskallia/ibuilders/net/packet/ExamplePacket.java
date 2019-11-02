package iskallia.ibuilders.net.packet;

import iskallia.ibuilders.net.context.ClientContext;
import iskallia.ibuilders.net.context.ServerContext;
import iskallia.ibuilders.net.packet.util.C2SMessage;
import iskallia.ibuilders.net.packet.util.S2CMessage;

import java.util.Calendar;

public class ExamplePacket extends Packet implements C2SMessage, S2CMessage {

    private String message;

    /*The packet is magically built using GSON, just set the value to a field and it takes care of the rest.*/
    public ExamplePacket(String message) {
        this.message = message;
    }

    /*Handle the packet when received server-side.*/
    @Override
    public Packet onPacketReceived(ServerContext context) {
        Calendar date = Calendar.getInstance();
        String newMessage =
                        "[" + date.get(Calendar.HOUR_OF_DAY) + ":"
                        + date.get(Calendar.MINUTE) + ":"
                        + date.get(Calendar.SECOND) + "]" +
                        "[" + context.listener.getListenerId() + "]: " + this.message;

        context.serverListener.sendPacketToAll(new ExamplePacket(newMessage));
        System.out.println(newMessage);
        return null;
    }

    /*Handle the packet when received client-side.*/
    @Override
    public Packet onPacketReceived(ClientContext context) {
        System.out.println(this.message);
        return null;
    }

}
