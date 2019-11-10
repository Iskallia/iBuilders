package iskallia.ibuilders.init;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.net.packet.PacketC2SHandshake;
import iskallia.ibuilders.net.packet.PacketRequestSchemaInfo;
import iskallia.ibuilders.net.packet.PacketRequestSchematic;
import iskallia.ibuilders.net.packet.PacketS2CDisconnect;
import iskallia.ibuilders.net.packet.mc.C2SCreatorAction;
import iskallia.ibuilders.net.packet.mc.C2STerminalAction;
import iskallia.ibuilders.net.packet.mc.S2CSchemaInfo;
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
        PIPELINE.registerMessage(C2STerminalAction.Handler.class, C2STerminalAction.class, nextId(), Side.SERVER);
        PIPELINE.registerMessage(C2SCreatorAction.Handler.class, C2SCreatorAction.class, nextId(), Side.SERVER);
        PIPELINE.registerMessage(S2CSchemaInfo.Handler.class, S2CSchemaInfo.class, nextId(), Side.CLIENT);
    }

    private static int nextId() {
        return packetId++;
    }

}

