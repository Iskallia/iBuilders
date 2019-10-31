package iskallia.ibuilders.net.packet;

import iskallia.ibuilders.net.context.ServerContext;

public interface C2SMessage {

    Packet onPacketReceived(ServerContext context);

}
