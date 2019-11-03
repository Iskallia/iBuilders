package iskallia.ibuilders.init;

import iskallia.ibuilders.net.packet.PacketC2SHandshake;
import iskallia.ibuilders.net.packet.PacketRequestSchemaInfo;
import iskallia.ibuilders.net.packet.PacketRequestSchematic;
import iskallia.ibuilders.net.packet.PacketS2CDisconnect;
import iskallia.ibuilders.net.packet.util.PacketRegistry;

public class InitPacket {

    public static void registerPackets() {
        PacketRegistry.registerPacket(PacketC2SHandshake.class);
        PacketRegistry.registerPacket(PacketS2CDisconnect.class);
        PacketRegistry.registerPacket(PacketRequestSchemaInfo.class);
        PacketRegistry.registerPacket(PacketRequestSchematic.class);
    }


}

