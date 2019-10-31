package iskallia.ibuilders.net.packet;

import iskallia.ibuilders.net.context.ClientContext;

public interface S2CMessage {

    Packet onPacketReceived(ClientContext context);

}
