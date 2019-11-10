package iskallia.ibuilders.init;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.net.packet.PacketC2SHandshake;
import iskallia.ibuilders.net.packet.PacketRequestSchemaInfo;
import iskallia.ibuilders.net.packet.PacketRequestSchematic;
import iskallia.ibuilders.net.packet.PacketS2CDisconnect;
import iskallia.ibuilders.net.packet.mc.C2SUploadSchematic;
import iskallia.ibuilders.net.packet.mc.S2CUserUploads;
import iskallia.ibuilders.net.packet.util.PacketRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class InitPacket {

    public static SimpleNetworkWrapper PIPELINE = null;
    private static int packetId = 0;

    public static void registerPackets() {
        PacketRegistry.registerPacket(PacketC2SHandshake.class);
        PacketRegistry.registerPacket(PacketS2CDisconnect.class);
        PacketRegistry.registerPacket(PacketRequestSchemaInfo.class);
        PacketRegistry.registerPacket(PacketRequestSchematic.class);
    }

    public static void registerMCPackets() {
        PIPELINE = NetworkRegistry.INSTANCE.newSimpleChannel(Builders.MOD_ID);
        PIPELINE.registerMessage(C2SUploadSchematic.Handler.class, C2SUploadSchematic.class, nextId(), Side.SERVER);
        PIPELINE.registerMessage(S2CUserUploads.Handler.class, S2CUserUploads.class, nextId(), Side.CLIENT);
    }

    private static int nextId() {
        return packetId++;
    }

}

