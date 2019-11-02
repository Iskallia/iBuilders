package iskallia.ibuilders.net.packet.util;

import iskallia.ibuilders.net.context.ClientContext;
import iskallia.ibuilders.net.packet.Packet;

public interface S2CMessage {

    Packet onPacketReceived(ClientContext context);

}
