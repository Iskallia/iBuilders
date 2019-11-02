package iskallia.ibuilders.net.packet.util;

import iskallia.ibuilders.net.context.ServerContext;
import iskallia.ibuilders.net.packet.Packet;

public interface C2SMessage {

    Packet onPacketReceived(ServerContext context);

}
