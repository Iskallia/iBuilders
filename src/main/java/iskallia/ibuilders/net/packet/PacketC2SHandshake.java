package iskallia.ibuilders.net.packet;

import iskallia.ibuilders.net.context.ServerContext;
import iskallia.ibuilders.net.packet.util.C2SMessage;

public class PacketC2SHandshake extends Packet implements C2SMessage {

    public PacketC2SHandshake(String version) {

    }

    @Override
    public Packet onPacketReceived(ServerContext context) {
        return null;
    }


}
