package iskallia.ibuilders.net.packet;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.net.connection.Listener;
import iskallia.ibuilders.net.context.ServerContext;
import iskallia.ibuilders.net.packet.util.C2SMessage;
import iskallia.ibuilders.net.packet.util.PacketRegistry;
import org.jline.utils.Log;

import java.util.List;

public class PacketC2SHandshake extends Packet implements C2SMessage {

    private String version;
    private List<String> packetRegistry;

    public PacketC2SHandshake() {
        this.version = Builders.MOD_VERSION;
        this.packetRegistry = PacketRegistry.getRegistry();
    }

    @Override
    public Packet onPacketReceived(ServerContext context) {
        Listener listener = context.listener;

        if(!this.version.equals(Builders.MOD_VERSION)) {
            Builders.LOG.error("Main server [" + listener.getConnectionAddress() + "] is running version "
                    + this.version + " while this plot server is running " + Builders.MOD_VERSION + ".");

            context.minecraftServer.addScheduledTask(listener::disconnect);
            return new PacketS2CDisconnect(PacketS2CDisconnect.Reason.OUTDATED_VERSION);
        } else if(!this.packetRegistry.equals(PacketRegistry.getRegistry())) {
            Builders.LOG.error("Main server [" + listener.getConnectionAddress() + "] has a different packet registry.");

            context.minecraftServer.addScheduledTask(listener::disconnect);
            return new PacketS2CDisconnect(PacketS2CDisconnect.Reason.INVALID_PROTOCOL);
        } else {
            Builders.LOG.warn("Handshake with main server [" + listener.getConnectionAddress() + "] was successful!");
        }

        return null;
    }


}
