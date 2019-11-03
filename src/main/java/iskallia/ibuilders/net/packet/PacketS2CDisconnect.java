package iskallia.ibuilders.net.packet;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.net.context.ClientContext;
import iskallia.ibuilders.net.packet.util.S2CMessage;

public class PacketS2CDisconnect extends Packet implements S2CMessage {

    private Reason reason;

    private PacketS2CDisconnect() {

    }

    public PacketS2CDisconnect(Reason reason) {
        this.reason = reason;
    }

    @Override
    public Packet onPacketReceived(ClientContext context) {
        Builders.LOG.error("Disconnecting from plot server [" + context.listener.getConnectionAddress()
                + "] for reason: " + this.reason + ".");
        return null;
    }

    public enum Reason {
        INVALID_PROTOCOL, OUTDATED_VERSION
    }

}
